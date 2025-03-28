package fi.tampere.sourcematerial;

import fi.mml.map.mapwindow.util.OskariLayerWorker;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionDeniedException;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.ConversionHelper;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ResponseHelper;
import org.json.JSONObject;
import org.oskari.permissions.PermissionService;
import org.oskari.permissions.model.PermissionType;
import org.oskari.permissions.model.Resource;
import org.oskari.permissions.model.ResourceType;

import java.util.*;
import java.util.stream.Collectors;

@OskariActionRoute("SourceMaterial")
public class SourceMaterialHandler extends RestActionHandler {

    private static final Logger LOG = LogFactory.getLogger(SourceMaterial.class);

    public void preProcess(ActionParameters params) throws ActionException {
        // 1) Check that the user has a role with LAHTO_prefix or ActionDenied
        if (getValidRoles(params.getUser()).isEmpty()) {
            throw new ActionDeniedException("Nope");
        }
    }

    public void handleGet(ActionParameters params) throws ActionException {
        // 2) find user roles starting with LAHTO_-prefix
        JSONObject response = new JSONObject();
        List<Resource> resources = getPermissionResources();

        OskariLayerService service = OskariComponentManager.getComponentOfType(OskariLayerService.class);
        for (Role role : getValidRoles(params.getUser())) {
            Set<Integer> layerIds = getMapLayers(resources, role);
            // 3) find layers that have permissions for each LAHTO_-prefixed role
            JSONObject layers = OskariLayerWorker.getListOfMapLayers(
                    service.findByIdList(new ArrayList<>(layerIds)),
                    params.getUser(),
                    params.getLocale().getLanguage(),
                    "EPSG:3067",
                    true,
                    true);
            JSONHelper.putValue(response, role.getName(), layers.optJSONArray("layers"));
        }
        // layers for the background layer selection
        JSONObject layers = OskariLayerWorker.getListOfMapLayers(
                service.findByIdList(new ArrayList(SourceMaterial.getBaseLayerIds())),
                params.getUser(),
                params.getLocale().getLanguage(),
                "EPSG:3067",
                true,
                true);
        JSONHelper.putValue(response, "__layers", layers.optJSONArray("layers"));

        // 4) respond with object having role names as keys and layer json arrays as values
        ResponseHelper.writeResponse(params, response);
    }

    private Set<Role> getValidRoles(User user) {
        return user.getRoles()
                .stream()
                .filter(r -> r.getName().startsWith(SourceMaterial.ROLE_PREFIX))
                .collect(Collectors.toSet());
    }

    private List<Resource> getPermissionResources() {
        PermissionService service = OskariComponentManager.getComponentOfType(PermissionService.class);
        return service.findResourcesByType(ResourceType.maplayer);
    }
    private Set<Integer> getMapLayers(List<Resource> resources, Role role) {
        // find layers that have VIEW_LAYER permission for a LAHTO_-prefixed role
        return resources.stream()
                .filter(r -> r.hasPermission(role, PermissionType.VIEW_LAYER))
                .map(r -> r.getMapping())
                .map(id -> ConversionHelper.getInt(id, -1))
                .filter(id -> id != -1)
                .collect(Collectors.toSet());
    }

}
