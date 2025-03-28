package flyway.tre;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;
import org.oskari.helpers.BundleHelper;

public class V1_0_7__add_layerswipe extends BaseJavaMigration {

    public void migrate(Context context) throws Exception {
        BundleHelper.registerBundle(context.getConnection(), "layerswipe");
        AppSetupHelper.addBundleToApps(context.getConnection(), "layerswipe");
    }

}
