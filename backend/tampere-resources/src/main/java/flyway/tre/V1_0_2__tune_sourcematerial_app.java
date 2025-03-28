package flyway.tre;

import fi.nls.oskari.control.view.modifier.bundle.BundleHandler;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.tampere.sourcematerial.SourceMaterial;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oskari.helpers.AppSetupHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class V1_0_2__tune_sourcematerial_app extends BaseJavaMigration {
    public void migrate(Context context) throws Exception {
        Connection conn = context.getConnection();
        List<Long> ids = AppSetupHelper.getSetupsForApplicationByType(conn, "sourcematerial");
        for (Long id: ids) {
            Bundle mapfull = AppSetupHelper.getAppBundle(conn, id, BundleHandler.BUNDLE_MAPFULL);
            addLayerSelection(mapfull);
            changeDefaultLayer(mapfull);
            AppSetupHelper.updateAppBundle(conn, id, mapfull);
        }
    }

    /**
     *
     {
     "id": "Oskari.mapframework.bundle.mapmodule.plugin.BackgroundLayerSelectionPlugin",
     "config": {
     "baseLayers": [
         "1918",
         "2266",
         "6",
         "18"
     ],
     "showAsDropdown": false
     }
     }
     * @param mapfull
     * @throws SQLException
     */
    private void addLayerSelection(Bundle mapfull) throws Exception  {
        JSONArray plugins = mapfull.getConfigJSON().optJSONArray("plugins");
        JSONObject plugin = new JSONObject();
        plugin.put("id", "Oskari.mapframework.bundle.mapmodule.plugin.BackgroundLayerSelectionPlugin");
        JSONObject config = new JSONObject();
        JSONArray layerIds = new JSONArray(SourceMaterial.getBaseLayerIds());
        config.put("baseLayers", layerIds);
        plugin.put("config", config);
        config.put("showAsDropdown", false);
        plugins.put(plugin);
    }

    /**
     * {
     *     "east": 327304.66825,
     *     "selectedLayers": [
     *         {
     *             "id": 1918
     *         }
     *     ],
     *     "north": 6822494.74555,
     *     "zoom": 8
     * }
     * @param mapfull
     * @throws Exception
     */
    private void changeDefaultLayer(Bundle mapfull) throws Exception {
        JSONArray layers = mapfull.getStateJSON().optJSONArray("selectedLayers");
        // https://georaster.tampere.fi/geoserver/gwc/service/wmts?
        // georaster:virastokartta_vari_EPSG_3067
        layers.getJSONObject(0).put("id", 18);
    }
}
