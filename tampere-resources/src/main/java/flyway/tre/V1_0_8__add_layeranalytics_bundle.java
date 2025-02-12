package flyway.tre;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;

/**
 * Adds layer analytics bundle to embedded map views
 */

public class V1_0_8__add_layeranalytics_bundle extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        AppSetupHelper.addBundleToApps(context.getConnection(), "layeranalytics");
    }
}
