package flyway.tre;

import fi.nls.oskari.control.view.modifier.bundle.BundleHandler;
import fi.nls.oskari.domain.map.view.Bundle;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oskari.helpers.AppSetupHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class V1_0_5__remove_terms_from_logoplugin extends BaseJavaMigration {
    public void migrate(Context context) throws Exception {
        Connection conn = context.getConnection();
        List<Long> ids = AppSetupHelper.getSetupsForUserAndDefaultType(conn);
        for (Long id: ids) {
            Bundle mapfull = AppSetupHelper.getAppBundle(conn, id, BundleHandler.BUNDLE_MAPFULL);
            modifyLogoPlugin(mapfull);
            AppSetupHelper.updateAppBundle(conn, id, mapfull);
        }
    }

    private void modifyLogoPlugin(Bundle mapfull) throws Exception  {
        JSONArray plugins = mapfull.getConfigJSON().optJSONArray("plugins");
        for(int i = 0; i < plugins.length(); i++) {
            JSONObject plugin = plugins.optJSONObject(i);
            if (plugin == null) {
                continue;
            }
            if (!"Oskari.mapframework.bundle.mapmodule.plugin.LogoPlugin".equals(plugin.optString("id"))) {
                continue;
            }
            JSONObject config = plugin.optJSONObject("config");
            if (config == null) {
                continue;
            }
            config.remove("termsUrl");
        }
    }
}
