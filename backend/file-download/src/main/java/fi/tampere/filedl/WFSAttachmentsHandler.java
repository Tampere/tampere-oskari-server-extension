package fi.tampere.filedl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.mml.map.mapwindow.util.OskariLayerWorker;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.util.ConversionHelper;
import fi.nls.oskari.util.IOHelper;
import fi.nls.oskari.util.ResponseHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oskari.log.AuditLog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OskariActionRoute("WFSAttachments")
public class WFSAttachmentsHandler extends RestActionHandler {

    private static final Logger LOG = LogFactory.getLogger(WFSAttachmentsHandler.class);

    private static final int MB = 1024 * 1024;
    // Store files smaller than 16mb in memory instead of writing them to disk
    private static final int MAX_SIZE_MEMORY = 16 * MB;
    private static final DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory(MAX_SIZE_MEMORY, null);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String PARAM_LAYER = "layerId";
    private static final String PARAM_LAYER_JSON = "json";

    private FileService service;
    private FileDownloadAccessLogService accessLog;

    public void init() {
        super.init();
        service = new FileServiceMybatisImpl();
        accessLog = new FileDownloadAccessLogServiceMybatisImpl();
    }

    public void handleGet(ActionParameters params) throws ActionException {
        int layerId = params.getHttpParam(PARAM_LAYER, -1);
        if (layerId == -1) {
            // return list of layer ids with attachments
            List<Integer> layerIds = service.getLayersWithFiles();
            ResponseHelper.writeResponse(params, getLayerResponse(layerIds, params));
            return;
        }
        int fileId = params.getHttpParam("fileId", -1);
        // return file
        if (fileId != -1) {
            try (WFSAttachmentFile file = service.getFile(layerId, fileId)) {
                writeFileResponse(file, params.getResponse(), params.getLocale().getLanguage());
            } catch (IOException | ServiceException e) {
                throw new ActionException("Error reading file", e);
            }
            accessLog.logFileRead(params.getUser(), layerId, fileId);
            return;
        }
        String featureId = params.getHttpParam("featureId");
        String externalFile = params.getHttpParam("name");

        // download external file if param is present
        if (externalFile != null) {
            // try with resource to close inputstream
            try (WFSAttachmentFile file = service.getExternalFile(layerId, featureId, externalFile)) {
                writeFileResponse(file, params.getResponse(), params.getLocale().getLanguage());
            } catch (IOException | ServiceException e) {
                AuditLog.user(params.getClientIp(), params.getUser()).
                        withParam("layer", layerId).
                        withParam("featureId", featureId).
                        withParam("file", externalFile).
                        errored(getName());
                ResponseHelper.writeError(params, "File not found", HttpServletResponse.SC_NOT_FOUND);
            }
            accessLog.logExternalFileRead(params.getUser(), layerId, featureId, externalFile);
            return;
        }

        // list files
        JSONArray files = null;
        try {
            if (featureId == null) {
                files = getFilesForLayer(layerId);
            } else {
                // or list files that are available for layer/feature
                files = getFilesForFeature(layerId, featureId);
            }
        } catch (Exception e) {
            LOG.error(e, "Error reading files");
            ResponseHelper.writeResponse(params, new JSONArray());
        }

        ResponseHelper.writeResponse(params, files);
    }

    private JSONArray getLayerResponse(List<Integer> layerIds, ActionParameters params) {
        boolean writeLayersJSON = params.getHttpParam(PARAM_LAYER_JSON, false);
        if (!writeLayersJSON) {
            return new JSONArray(layerIds);
        }
        // write layer json, CRS doesn't matter as these are all WFS-layers
        JSONObject layers = OskariLayerWorker.getListOfMapLayersByIdList(
                layerIds, params.getUser(), params.getLocale().getLanguage(),
                null);

        return layers.optJSONArray("layers");
    }

    private void writeFileResponse(WFSAttachmentFile file, HttpServletResponse response, String language) throws IOException {
        response.setContentType(getContentType(file.getFileExtension()));
        // attachment header
        response.addHeader("Content-Disposition", "attachment; filename=\"" + getFilename(file, language) + "\"");
        response.setContentLengthLong(file.getSize());
        try (OutputStream out = response.getOutputStream()) {
            IOHelper.copy(file.getFile(), out);
        }
    }

    private JSONArray getFilesForLayer(int layerId) throws Exception {
        String files = MAPPER.writeValueAsString(service.getFiles(layerId));
        return new JSONArray(files);
    }

    private JSONArray getFilesForFeature(int layerId, String featureId) throws Exception {
        List<WFSAttachment> list = service.getFiles(layerId, featureId);
        if (list.isEmpty()) {
            list = service.getExternalFiles(layerId, featureId);
        }
        String files = MAPPER.writeValueAsString(list);
        return new JSONArray(files);
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        params.requireAdminUser();
        // TODO: check permissions for non-admin

        List<FileItem> fileItems = parseRequest(params.getRequest());
        Map<String, String> parameters = getFormParams(fileItems);
        fileItems = getFiles(fileItems);
        int layerId = ConversionHelper.getInt(parameters.get(PARAM_LAYER), -1);
        if (layerId == -1) {
            throw new ActionParamsException("Layer id required");
        }

        try {
            List<WFSAttachment> writtenFiles = new ArrayList<>(fileItems.size());
            for (FileItem f : fileItems) {
                try (WFSAttachmentFile file = new WFSAttachmentFile(f.getInputStream(), f.getSize())) {
                    String[] name = FileService.getBaseAndExtension(f.getName());
                    file.setFeatureId(name[0]);
                    file.setLayerId(layerId);
                    file.setFileExtension(name[1]);
                    String locale = parameters.get("locale_" + f.getName());
                    if (locale == null) {
                        locale = name[0];
                    }
                    LOG.info("Writing", f.getName(), f.isInMemory(), f.getSize());
                    file.setLocale(locale);
                    writtenFiles.add(service.insertFile(file));
                    LOG.info("Wrote", f.getName());
                }
                f.delete();
            }

            AuditLog.user(params.getClientIp(), params.getUser()).
                    withParam("layer", layerId).
                    withParam("files", writtenFiles.stream().map(
                            f -> f.getFeatureId() + " (" + f.getLocale() + ")")
                            .collect(Collectors.joining(","))).
                    added(getName());
            ResponseHelper.writeResponse(params, new JSONArray(MAPPER.writeValueAsString(writtenFiles)));
        } catch (JSONException | IOException | ServiceException e) {
            throw new ActionException("Unable to save files", e);
        } finally {
            fileItems.forEach(FileItem::delete);
        }
    }

    @Override
    public void handlePut(ActionParameters params) throws ActionException {
        params.requireAdminUser();
        // TODO: check permissions for non-admin
        WFSAttachment file = new WFSAttachment();
        file.setId(params.getRequiredParamInt("fileId"));
        file.setLocale(params.getRequiredParam("locale"));
        service.updateMetadata(file);
        AuditLog.user(params.getClientIp(), params.getUser()).
                withParam("id", file.getId()).
                updated(getName());
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        params.requireAdminUser();
        // TODO: check permissions for non-admin
        try {
            WFSAttachment file = service.removeFile(
                    params.getRequiredParamInt(PARAM_LAYER), params.getRequiredParamInt("fileId"));
            AuditLog.user(params.getClientIp(), params.getUser()).
                    withParam("id", file.getId()).
                    withParam("layer", file.getLayerId()).
                    withParam("featureId", file.getFeatureId()).
                    withParam("file", file.getLocale()).
                    deleted(getName());
            ResponseHelper.writeResponse(params, HttpServletResponse.SC_OK, ResponseHelper.CONTENT_TYPE_JSON_UTF8, MAPPER.writeValueAsString(file));
        } catch (Exception e) {
            throw new ActionException("File removal errored", e);
        }
    }

    private String getContentType(String extension) {
        switch (extension) {
        case "gpkg":
            return "application/geopackage+sqlite3";
        case "tif":
            return "image/tiff";
        case "las":
        default:
            return "application/octet-stream";
        }
    }

    private String getFilename(WFSAttachment file, String lang) {
        final String separator = "_";
        StringWriter w = new StringWriter();
        w.write(getLayerName(file.getLayerId(), lang));
        w.write(separator);
        w.write(file.getFeatureId());
        w.write(separator);
        // TODO: might need urlencoding or maybe check !file.isExternal() -> file.getId()
        w.write(file.getLocale());
        w.write(".");
        w.write(file.getFileExtension());
        return w.toString();
    }

    private String getLayerName(int id, String lang) {
        try {
            OskariLayer layer = OskariComponentManager.getComponentOfType(OskariLayerService.class).find(id);
            return layer.getName(lang);
        } catch (Exception e) {
            LOG.warn(e, "Error getting layer name");
        }
        return Integer.toString(id);
    }

    private List<FileItem> parseRequest(HttpServletRequest request) throws ActionException {
        try {
            request.setCharacterEncoding("UTF-8");
            ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
            return upload.parseRequest(request);
        } catch (UnsupportedEncodingException | FileUploadException e) {
            throw new ActionException("Failed to read request", e);
        }
    }

    private List<FileItem> getFiles(List<FileItem> fileItems) {
        return fileItems.stream()
                .filter(f -> !f.isFormField())
                .collect(Collectors.toList());
    }

    private Map<String, String> getFormParams(List<FileItem> fileItems) {
        return fileItems.stream()
                .filter(f -> f.isFormField())
                .collect(Collectors.toMap(
                        f -> f.getFieldName(),
                        f -> new String(f.get(), StandardCharsets.UTF_8)));
    }

}
