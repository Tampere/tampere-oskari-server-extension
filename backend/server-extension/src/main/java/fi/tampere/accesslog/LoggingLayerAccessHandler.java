package fi.tampere.accesslog;

import fi.nls.oskari.annotation.Oskari;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.json.JSONObject;
import org.oskari.service.user.LayerAccessHandler;

import fi.nls.oskari.domain.User;
import fi.nls.oskari.domain.map.OskariLayer;


@Oskari
public class LoggingLayerAccessHandler extends LayerAccessHandler {

    public static final String ATTRIBUTE_SENSITIVE = "sensitive";
    private static final Logger LOG = LogFactory.getLogger("ACCESS");

    @Override
    public void handle(OskariLayer layer, User user) {
        if (!isSensitive(layer) || user.isGuest()) {
            return;
        }

        String layerName = layer.getName();
        String layerUrl = layer.getUrl();
        long userId = user.getId();
        String userScreenName = user.getScreenname();
        audit(layerName, layerUrl, userId, userScreenName);
    }

    public boolean isSensitive(OskariLayer layer) {
        JSONObject attributes = layer.getAttributes();
        return attributes != null ? attributes.optBoolean(ATTRIBUTE_SENSITIVE, false) : false;
    }

    private void audit(String layerName, String layerUrl, long userId, String userScreenName) {
        LOG.warn(userId, userScreenName, layerName, layerUrl);
    }
}
