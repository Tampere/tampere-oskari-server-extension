
// From file-upload bundle
import { FileService } from '../../file-upload/service/FileService';

let layers = null;
export const getLayers = (callback) => {
    if (layers && layers.length) {
        callback(layers);
        return;
    }

    const service = Oskari.getSandbox().getService('Oskari.mapframework.service.MapLayerService');
    FileService.listLayersWithFiles(layersJSON => {
        // write to "global"
        layers = layersJSON.map(json => {
            var mapLayer = service.createMapLayer(json);
            // unsupported maplayer type returns null so check for it
            if (mapLayer) {
                service.addLayer(mapLayer);
                return mapLayer;
            }
        }
        // filter out null layers (unsupported)
        ).filter(layer => !!layer);
        // call render
        callback(layers);
    }, true);
};

export const getLayerFromService = (id) => {
    const service = Oskari.getSandbox().getService('Oskari.mapframework.service.MapLayerService');
    return service.findMapLayer(id);
};
