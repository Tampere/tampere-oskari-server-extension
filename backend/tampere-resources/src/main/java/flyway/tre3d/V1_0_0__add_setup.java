package flyway.tre3d;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;
import org.oskari.helpers.LayerHelper;

public class V1_0_0__add_setup extends BaseJavaMigration {

    public void migrate(Context context) throws Exception {
        LayerHelper.setupLayer("buildings3d.json");
        LayerHelper.setupLayer("mesh3d.json");
        AppSetupHelper.create(context.getConnection(), "geoportal3d.json");
    }
}
