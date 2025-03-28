
import { Cluster as olCluster } from 'ol/source';
import olPoint from 'ol/geom/Point';
import olLineString from 'ol/geom/LineString';
import olLinearRing from 'ol/geom/LinearRing';
import olPolygon from 'ol/geom/Polygon';
import olMultiPoint from 'ol/geom/MultiPoint';
import olMultiLineString from 'ol/geom/MultiLineString';
import olMultiPolygon from 'ol/geom/MultiPolygon';
import olGeometryCollection from 'ol/geom/GeometryCollection';

import GeoJSONReader from 'jsts/org/locationtech/jts/io/GeoJSONReader';
import OL3Parser from 'jsts/org/locationtech/jts/io/OL3Parser';
import RelateOp from 'jsts/org/locationtech/jts/operation/relate/RelateOp';
const reader = new GeoJSONReader();
const olParser = new OL3Parser();
olParser.inject(olPoint, olLineString, olLinearRing, olPolygon, olMultiPoint, olMultiLineString, olMultiPolygon, olGeometryCollection);

const WFS_ID_KEY = '_oid';

export const getFeaturesInGeom = (geometry, layerId) => {
    if (!geometry || !layerId) {
        return [];
    }
    const olLayers = Oskari.getSandbox().findRegisteredModuleInstance('MainMapModule').getOLMapLayers(layerId);
    if (!olLayers || olLayers.length !== 1) {
        return [];
    }
    const layer = olLayers[0];
    const featuresById = new Map();
    const geomFilter = reader.read(geometry);
    const envelope = geomFilter.getEnvelopeInternal();
    const extentFilter = [envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY()];
    let source = layer.getSource();
    if (source instanceof olCluster) {
        // Get wrapped vector source
        source = source.getSource();
    }
    source.forEachFeatureInExtent(extentFilter, ftr => {
        const geom = olParser.read(ftr.getGeometry());
        if (RelateOp.relate(geomFilter, geom).isIntersects()) {
            featuresById.set(ftr.get(WFS_ID_KEY), ftr);
        }
    });
    return Array.from(featuresById.values());
};
