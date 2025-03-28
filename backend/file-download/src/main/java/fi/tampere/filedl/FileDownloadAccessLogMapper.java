package fi.tampere.filedl;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface FileDownloadAccessLogMapper {

    @Insert("INSERT INTO tampere_filedl_log_file (user_id, layer_id, file_id) " +
            "VALUES (#{userId}, #{layerId}, #{fileId})")
    void insertFile(@Param("userId") int userId, @Param("layerId") int layerId, @Param("fileId") int fileId);

    @Insert("INSERT INTO tampere_filedl_log_external (user_id, layer_id, feature_id, name) " +
            "VALUES (#{userId}, #{layerId}, #{featureId}, #{name})")
    void insertExternal(@Param("userId") int userId, @Param("layerId") int layerId, @Param("featureId") String featureId, @Param("name") String name);

}
