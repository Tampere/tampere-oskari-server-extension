package fi.tampere.filedl;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import org.json.JSONObject;
import org.oskari.log.AuditLog;

@OskariActionRoute("WFSAttachmentsLayer")
public class  WFSAttachmentsLayerHandler extends RestActionHandler {

    private static final String PARAM_LAYER = "layerId";
    private static final String KEY_ATTACHMENT_KEY = "attachmentKey";

    public void handlePut(ActionParameters params) throws ActionException {
        params.requireAdminUser();
        int layerId = params.getRequiredParamInt(PARAM_LAYER);
        String attachmentKey = params.getRequiredParam(KEY_ATTACHMENT_KEY);
        OskariLayerService service = OskariComponentManager.getComponentOfType(OskariLayerService.class);
        OskariLayer layer = service.find(layerId);
        layer.addAttribute(KEY_ATTACHMENT_KEY, attachmentKey);
        service.update(layer);

        AuditLog.user(params.getClientIp(), params.getUser())
                .withParam("layerId", layerId)
                .withParam(KEY_ATTACHMENT_KEY, attachmentKey)
                .updated("WFSAttachments");
        JSONObject json = JSONHelper.createJSONObject(PARAM_LAYER, layerId);
        JSONHelper.putValue(json, KEY_ATTACHMENT_KEY, attachmentKey);
        ResponseHelper.writeResponse(params, json);
    }

}
