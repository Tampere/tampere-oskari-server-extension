package fi.tampere.filedl;

import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.util.PropertyUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileService {

    private final String fileStorage = PropertyUtil.get("file.storage.folder", "/files");

    public WFSAttachmentFile getFile(int layerId, int fileId) throws ServiceException {
        // TODO: return inputstream to file
        return null;
    }

    public List<WFSAttachment> getFiles(int layerId) throws ServiceException {

        Path path = Paths.get(fileStorage, Integer.toString(layerId));
        if (!Files.exists(path)) {
            return Collections.EMPTY_LIST;
        }
        try {
            return Files.list(path)
                    .map(p -> p.toFile())
                    .filter(f -> f.canRead() && !f.isDirectory())
                    .map(f -> new WFSAttachment())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ServiceException("Unable to read files", e);
        }
    }

    public List<WFSAttachment> getFiles(int layerId, String featureId) throws ServiceException {
        return getFiles(layerId).stream()
                .filter(f -> f.getFeatureId().equalsIgnoreCase(featureId))
                .collect(Collectors.toList());
    }


    public WFSAttachment saveFile(int layerId, WFSAttachmentFile file) throws ServiceException {
        Path path = ensurePath(Integer.toString(layerId));
        try {
            // TODO: save db
            // save file
            String filename = file.getId() + "." + file.getFileExtension();
            Path mpath = Paths.get(path.toString(), filename);
            Files.copy(file.getFile(), mpath);
            // TODO: WFSAttachmentFile -> WFSAttachment
            file.setFile(null);
            return file;
        } catch (IOException e) {
            throw new ServiceException("Unable to save files", e);
        }
    }

    private Path ensurePath(String layer) throws ServiceException {
        Path path = Paths.get(fileStorage, layer);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                throw new ServiceException("Unable to create folder for " + layer, e );
            }
        }
        return path;
    }
}