package fi.tampere.filedl;

import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.util.PropertyUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class FileService {

    protected static final String KEY_ATTACHMENT_PATH = "attachmentPath";

    private static final Logger LOG = LogFactory.getLogger(FileService.class);
    private final String fileStorage = PropertyUtil.get("file.storage.folder", "/files");

    /**
     * Filename should be featureID + extension
     * @param filename
     * @return array of length 2 with base as first and extension as second
     */
    public static String[] getBaseAndExtension(String filename) {
        String[] name = filename.split("\\.");
        if (name.length > 2) {
            String ext = name[name.length - 1];
            String id = filename.substring(0, filename.length() - ext.length() - 1);
            return new String[] { id, ext };
        }
        if (name.length < 2) {
            return new String[] { name[0], "" };
        }
        return name;
    }

    public abstract WFSAttachmentFile getFile(int layerId, int fileId) throws ServiceException;

    public abstract List<Integer> getLayersWithFiles();

    public abstract List<WFSAttachment> getFiles(int layerId);

    protected WFSAttachmentFile getFileFromDisk(int layerId, int fileId) throws ServiceException {
        Path path = Paths.get(fileStorage, Integer.toString(layerId), Integer.toString(fileId));
        if (!Files.exists(path)) {
            throw new ServiceException("File not found");
        }
        InputStream in = null;
        try {
            in = Files.newInputStream(path);
            long size = Files.size(path);
            WFSAttachmentFile file = new WFSAttachmentFile(in, size);
            file.setLayerId(layerId);
            file.setId(fileId);
            return file;
        } catch (IOException e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                    // Ignore me
                }
            }
            throw new ServiceException("Error reading file", e);
        }
    }

    public WFSAttachmentFile getExternalFile(int layerId, String featureId, String name) throws ServiceException {
        Optional<Path> basePath = getPathForExternalFiles(layerId, featureId);
        if (!basePath.isPresent()) {
            throw new ServiceException("Error reading file");
        }
        // strip out any .. references
        String fname = Paths.get(name).getFileName().toString();
        Path filePath = Paths.get(basePath.get().toString(), fname);
        InputStream in = null;
        try {
            in = Files.newInputStream(filePath);
            long size = Files.size(filePath);
            WFSAttachmentFile file = new WFSAttachmentFile(in, size);
            String[] filename = FileService.getBaseAndExtension(fname);
            file.setLocale(filename[0]);
            file.setFileExtension(filename[1]);
            file.setLayerId(layerId);
            file.setFeatureId(featureId);
            file.setExternal(true);
            return file;
        } catch (IOException e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                    // Ignore me
                }
            }
            throw new ServiceException("Error reading file", e);
        }
    }

    private Optional<Path> getPathForExternalFiles(int layerId, String featureId) {
        OskariLayer layer = OskariComponentManager.getComponentOfType(OskariLayerService.class).find(layerId);
        if(layer == null) {
            return Optional.empty();
        }
        final String layerPath = layer.getAttributes().optString(KEY_ATTACHMENT_PATH);
        if(layerPath == null) {
            return Optional.empty();
        }
        // layer_basepath/feature_id/format.zip
        Path path = Paths.get(layerPath, featureId);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        return Optional.of(path);
    }

    public List<WFSAttachment> getExternalFiles(int layerId, String featureId) {
        Optional<Path> path = getPathForExternalFiles(layerId, featureId);
        if (!path.isPresent()) {
            return Collections.EMPTY_LIST;
        }
        File dir = path.get().toFile();
        List<WFSAttachment> attachments = new ArrayList<>();
        for (File f : dir.listFiles(f -> f.canRead() && !f.isDirectory())) {
            attachments.add(toWFSAttachment(f, layerId, featureId));
        }
        return attachments;
    }

    private WFSAttachment toWFSAttachment(File f, int layerId, String featureId) {
        WFSAttachment file = new WFSAttachment();
        String[] name = FileService.getBaseAndExtension(f.getName());
        file.setLocale(name[0]);
        file.setFileExtension(name[1]);
        file.setFeatureId(featureId);
        file.setLayerId(layerId);
        file.setExternal(true);
        return file;
    }

    public abstract List<WFSAttachment> getFiles(int layerId, String featureId);

    public abstract WFSAttachment insertFile(WFSAttachmentFile file) throws ServiceException;

    public WFSAttachment insertFileToDisk(WFSAttachmentFile file) throws ServiceException {

        Path path = ensurePath(Integer.toString(file.getLayerId()));
        try {
            // save file
            String filename = Integer.toString(file.getId());
            LOG.info("File path for", filename);
            Path mpath = Paths.get(path.toString(), filename);
            LOG.info("Copy contents");
            Files.copy(file.getFile(), mpath);
            LOG.info("Copied contents");
            return file.getMetadata();
        } catch (IOException e) {
            throw new ServiceException("Unable to save files", e);
        }
    }

    public abstract void updateMetadata(WFSAttachment file);

    private Path ensurePath(String layer) throws ServiceException {
        LOG.info("Checking path for", fileStorage, layer);
        Path path = Paths.get(fileStorage, layer);
        if (!Files.exists(path)) {
            LOG.info("Creating path");
            try {
                Files.createDirectory(path);
                LOG.info("Created path");
            } catch (Exception e) {
                throw new ServiceException("Unable to create folder for " + layer, e);
            }
        } else {
            LOG.info("Path existed");
        }
        return path;
    }

    public abstract WFSAttachment removeFile(int layerId, int fileId) throws ServiceException;

    public WFSAttachment removeFileFromDisk(int layerId, int fileId) throws ServiceException {
        // WFSAttachment file = findFile(fileId);
        Path path = Paths.get(fileStorage, Integer.toString(layerId), Integer.toString(fileId));
        if (!Files.exists(path)) {
            throw new ServiceException("File not found");
        }
        path.toFile().delete();
        WFSAttachment file = new WFSAttachment();
        file.setLayerId(layerId);
        file.setId(fileId);
        return file;
    }

}
