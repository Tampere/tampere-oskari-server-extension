package fi.tampere.filedl;

import fi.nls.oskari.domain.User;

public interface FileDownloadAccessLogService {

    public void logFileRead(User user, int layerId, int fileId);
    public void logExternalFileRead(User user, int layerId, String featureId, String name);

}
