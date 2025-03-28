Oskari.clazz.category('Oskari.mapframework.service.MapLayerService', 'geoserver-2.19.2-workaround', {
    // geoserver 2.19.2 has a bug that produces non-working styles (https://osgeo-org.atlassian.net/browse/GEOS-10213)
    // remove name when there's only one style so style is sent out as empty for GetMap calls
    _populateWmsMapLayerAdditionalData: function (layer, jsonLayer) {
        layer.setGfiContent(jsonLayer.gfiContent);
        var styles = jsonLayer.styles || [];
        if (styles.length === 1) {
            jsonLayer.styles[0].name = '';
        }
    },
    // fixing an issue in Oskari 2.5.1 with global legends for layers without styles.
    // Styles have no name so they are skipped even if they have a legend
    // https://github.com/oskariorg/oskari-frontend/pull/1689
    populateStyles: function (layer, styles) {
        if (!Array.isArray(styles)) {
            return;
        }
        const styleBuilder = Oskari.clazz.builder('Oskari.mapframework.domain.Style');
        styles.forEach(({ name, title, legend }) => {
            if (!name && !legend) {
                return;
            }
            const style = styleBuilder();
            style.setName(name);
            style.setTitle(title);
            style.setLegend(legend);
            layer.addStyle(style);
        });
    }
});
