package fi.tampere.filedl;


import org.oskari.user.User;

public interface FileDownloadAccessLogService {

    public void logFileRead(User user, int layerId, int fileId);
    public void logExternalFileRead(User user, int layerId, String featureId, String name);

}
