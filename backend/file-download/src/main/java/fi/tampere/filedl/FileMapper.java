package fi.tampere.filedl;

import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FileMapper {

        @Select("SELECT distinct layer_id FROM tampere_layer_attachment")
        List<Integer> findLayersWithFiles();

        @Select("SELECT distinct id FROM oskari_maplayer where attributes like '%" + FileService.KEY_ATTACHMENT_PATH + "%'")
        List<Integer> findLayersWithExternalFilepath();

        @Results(id = "FileResult", value = {
                @Result(property="id", column="id", id=true),
                @Result(property="featureId", column="feature_id"),
                @Result(property="layerId", column="layer_id"),
                @Result(property="locale", column="locale"),
                @Result(property="fileExtension", column="file_extension")
        })
        @Select("SELECT id,"
                + "feature_id,"
                + "layer_id,"
                + "locale,"
                + "file_extension "
                + "FROM tampere_layer_attachment "
                + "WHERE layer_id = #{id}")
        List<WFSAttachment> findByLayer(@Param("id") int id);

        @ResultMap("FileResult")
        @Select("SELECT id,"
                + "feature_id,"
                + "layer_id,"
                + "locale,"
                + "file_extension "
                + "FROM tampere_layer_attachment "
                + "WHERE layer_id = #{layerId} "
                + "AND feature_id = #{featureId}")
        List<WFSAttachment> findByLayerAndFeature(@Param("layerId") int layerId, @Param("featureId") String featureId);

        @ResultMap("FileResult")
        @Select("SELECT id,"
                + "feature_id,"
                + "layer_id,"
                + "locale,"
                + "file_extension "
                + "FROM tampere_layer_attachment "
                + "WHERE id = #{id}")
        WFSAttachment findFile(@Param("id") int fileId);

        @Insert("INSERT INTO tampere_layer_attachment (layer_id, feature_id, locale, file_extension) " +
                "VALUES (#{layerId}, #{featureId}, #{locale}, #{fileExtension})")
        @Options(useGeneratedKeys=true, keyColumn="id", keyProperty="id")
        void insertFile(WFSAttachment file);


        @Update("UPDATE tampere_layer_attachment SET " +
                "locale=#{locale} " +
                "WHERE id=#{id}")
        void update(final WFSAttachment file);

        @Delete("DELETE FROM tampere_layer_attachment WHERE id=#{id}")
        void deleteFile(int id);
}
