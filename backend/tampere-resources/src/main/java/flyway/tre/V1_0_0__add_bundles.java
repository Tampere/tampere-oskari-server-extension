package flyway.tre;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;
import org.oskari.helpers.BundleHelper;

// assumptions on the initial db:
// search has { "disableDefault": true }
// "hierarchical-layerlist"
// "timeseries"
public class V1_0_0__add_bundles extends BaseJavaMigration {
    public void migrate(Context context) throws Exception {
        // these should also be in initial db, but it doens't hurt with the helper either
        BundleHelper.registerBundle(context.getConnection(), "search-from-channels");
        BundleHelper.registerBundle(context.getConnection(), "selected-featuredata");
        BundleHelper.registerBundle(context.getConnection(), "file-upload");
        BundleHelper.registerBundle(context.getConnection(), "file-layerlist");

        // add applications based on json under /src/main/resources/json/views/
        // filedownload is assumed on existing db:

        //AppSetupHelper.create(context.getConnection(), "/json/view/filedownload.json");
        AppSetupHelper.create(context.getConnection(), "/json/views/sourcematerial.json");
    }
}
