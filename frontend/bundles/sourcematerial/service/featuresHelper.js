import GeoJSONReader from 'jsts/org/locationtech/jts/io/GeoJSONReader';
import RelateOp from 'jsts/org/locationtech/jts/operation/relate/RelateOp';

export const addFeaturesToMap = (features, opts = {}) => {
    var options = {
        /*
        featureStyle: {
            stroke: {
                color: '#00FF00',
                width: 5
            },
            fill: {
                color: '#0000FF'
            }
        },
        */
        ...opts
    };
    const geojson = wrapToCollection(features);
    Oskari.getSandbox().postRequestByName(
        'MapModulePlugin.AddFeaturesToMapRequest', [geojson, options]);
};
export const LAYER_SELECTION = 'SourceMaterialSelection';
export const LAYER_PREFIX = 'SourceMaterialFeatures_';
export const clearFeaturesFromLayer = (layerId) => {
    Oskari.getSandbox().postRequestByName(
        'MapModulePlugin.RemoveFeaturesFromMapRequest', [null, null, layerId]);
};

const wrapToCollection = (features = []) => {
    if (!Array.isArray(features)) {
        features = [features];
    }
    return {
        type: 'FeatureCollection',
        crs: Oskari.getSandbox().getMap().getSrsName() || 'EPSG:3067',
        features: features
    };
};

/**
 * Get bbox from feature/filter features by intersecting geometry
 */
const reader = new GeoJSONReader();
const readGeoJSONtoJSTS = (geometry) => {
    return reader.read(geometry);
};

export const getBboxFromFeature = (feature) => {
    if (!feature) {
        return null;
    }
    const geomFilter = readGeoJSONtoJSTS(feature.geometry);
    const envelope = geomFilter.getEnvelopeInternal();
    return [envelope.getMinX(), envelope.getMinY(),
        envelope.getMaxX(), envelope.getMaxY()];
};

// we fetch features withc bbox and filter more precisely on the browser with this
export const getFeaturesInsideGeometry = (features = [], filterGeometry) => {
    if (!filterGeometry || !features || !features.length) {
        return features;
    }
    const geomFilter = readGeoJSONtoJSTS(filterGeometry);
    return features.filter(feature =>
        RelateOp.relate(geomFilter, readGeoJSONtoJSTS(feature.geometry))
            .isIntersects());
};
