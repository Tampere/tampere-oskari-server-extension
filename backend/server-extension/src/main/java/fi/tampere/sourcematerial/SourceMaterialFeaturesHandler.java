package fi.tampere.sourcematerial;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.feature.GetWFSFeaturesHandler;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.IOHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Drop-in replacement for GetWFSFeaturesHandler but also tries to get features
 * for layers that are registered as WMS on Oskari.
 */
@OskariActionRoute("SourceMaterialFeatures")
public class SourceMaterialFeaturesHandler extends GetWFSFeaturesHandler {

    private static final Logger LOG = LogFactory.getLogger(SourceMaterial.class);

    @Override
    protected OskariLayer findMapLayer(String id, User user) throws ActionException {
        int layerId;
        try {
            layerId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new ActionParamsException(ERR_INVALID_ID);
        }
        OskariLayer layer = permissionHelper.getLayer(layerId, user);
        if (OskariLayer.TYPE_WFS.equals(layer.getType())) {
            return layer;
        }
        if (!OskariLayer.TYPE_WMS.equals(layer.getType())) {
            throw new ActionParamsException(ERR_LAYER_TYPE_NOT_WFS);
        }
        // create WFSLayer from WMS
        OskariLayer mock = new OskariLayer();
        mock.setUrl(getWFSUrlFromWMS(layer.getUrl()));
        mock.setType(OskariLayer.TYPE_WFS);
        mock.setName(layer.getName());
        // Credentials copying seem to be problematic.
        // There are WMS layers with credentials that when sent to the wfs don't work correctly
        mock.setUsername(layer.getUsername());
        mock.setPassword(layer.getPassword());
        mock.setVersion("1.1.0");
        LOG.debug("Original layer", layer.getUrl(), layer.getName());
        LOG.debug("WFS mock", mock.getUrl(), mock.getName());
        return mock;
    }

    private String getWFSUrlFromWMS(String wmsURL) throws ActionParamsException {
        String url = wmsURL.trim();
        // crude approach to change wms -> wfs
        // TODO: replace with call to DescribeLayer to get the url
        // -> cache url parsed from response with key == layerId, value = url from Describe
        String replacement = "/wfs?";
        if (url.endsWith("?")) {
            url = url.substring(0, url.length() -1);
        }
        if (url.endsWith("/wms")) {
            return url.substring(0, url.length() - 4) + replacement;
        } else if (url.endsWith("/ows")) {
            return url.substring(0, url.length() - 4) + replacement;
        }
        // handle cases when there's additional params
        if (url.indexOf("/wms?") != -1) {
            return url.replace("/wms?", replacement);
        }
        if (url.indexOf("/ows?") != -1) {
            return url.replace("/ows?", replacement);
        }
        // TODO: uppercase alternatives?
        throw new ActionParamsException("Not implemented yet: " + url);
    }

    private String getDescribeLayerUrl(OskariLayer layer) {
        Map<String, String> params = new HashMap<>();
        params.put("service", "WMS");
        params.put("request", "DescribeLayer");
        // server only supports 1.1.1
        params.put("version", "1.1.1");
        params.put("LAYERS", layer.getName());

        return IOHelper.constructUrl(layer.getUrl(), params);
    }

    /*
    https://geodata.tampere.fi/geoserver/joukkoliikenne/wms?
      service=WMS&version=1.1.1&request=DescribeLayer&LAYERS=raitiotiepysakit_keskusta_hervanta_tays

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE WMS_DescribeLayerResponse SYSTEM
    "https://geodata.tampere.fi/geoserver/schemas/wms/1.1.1/WMS_DescribeLayerResponse.dtd">
<WMS_DescribeLayerResponse version="1.1.1">
  <LayerDescription name="raitiotiepysakit_keskusta_hervanta_tays"
        wfs="https://geodata.tampere.fi/geoserver/joukkoliikenne/wfs?"
        owsURL="https://geodata.tampere.fi/geoserver/joukkoliikenne/wfs?" owsType="WFS">
    <Query typeName="joukkoliikenne:raitiotiepysakit_keskusta_hervanta_tays"/>
  </LayerDescription>
</WMS_DescribeLayerResponse>

     */
}
