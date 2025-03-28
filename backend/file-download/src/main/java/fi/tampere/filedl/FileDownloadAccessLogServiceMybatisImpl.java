package fi.tampere.filedl;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import fi.nls.oskari.db.DatasourceHelper;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.mybatis.MyBatisHelper;

public class FileDownloadAccessLogServiceMybatisImpl implements FileDownloadAccessLogService {

    private static final Class<FileDownloadAccessLogMapper> MAPPER = FileDownloadAccessLogMapper.class;
    private static final Logger LOG = LogFactory.getLogger(FileServiceMybatisImpl.class);

    private final SqlSessionFactory factory;

    public FileDownloadAccessLogServiceMybatisImpl() {
        this(DatasourceHelper.getInstance().getDataSource());
    }

    public FileDownloadAccessLogServiceMybatisImpl(DataSource ds) {
        if (ds == null) {
            LOG.warn("DataSource was null, all future calls will throw NPEs!");
            factory = null;
        } else {
            factory = MyBatisHelper.initMyBatis(ds, MAPPER);
        }
    }

    @Override
    public void logFileRead(User user, int layerId, int fileId) {
        try (SqlSession session = factory.openSession(true)) {
            session.getMapper(MAPPER).insertFile((int) user.getId(), layerId, fileId);
        }
    }

    @Override
    public void logExternalFileRead(User user, int layerId, String featureId, String name) {
        try (SqlSession session = factory.openSession(true)) {
            session.getMapper(MAPPER).insertExternal((int) user.getId(), layerId, featureId, name);
        }
    }

}
