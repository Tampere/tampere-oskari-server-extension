package flyway.tre;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.oskari.helpers.AppSetupHelper;

public class V1_0_4__add_announcements extends BaseJavaMigration {
    public void migrate(Context context) throws Exception {
        AppSetupHelper.addBundleToApps(context.getConnection(), "announcements");
    }

}
