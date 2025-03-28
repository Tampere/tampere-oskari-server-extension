import GeoJSON from 'ol/format/GeoJSON';
// From file-upload bundle
import { LayerHelper } from '../../file-upload/service/LayerHelper';
import { FileService } from '../../file-upload/service/FileService';

/**
 * Adds internal variables and injects files to features
 * @param {Array} features input features that will be enhanced with file links etc
 * @param {Function} callback returns processed features via callback
 */
export const processFeatures = (features, callback) => {
    const processedFeatures = [];
    // file listing is async so we need to gather results before calling original callback
    const receiveResult = (feat) => {
        processedFeatures.push(feat);
        if (processedFeatures.length === features.length) {
            callback(processedFeatures);
        }
    };
    features
        // normalize features between clicked and mass selection
        .map(feat => {
            const isMassSelection = !feat.geojson;
            if (isMassSelection) {
                // mass selection doesn't include geojson
                return { ...feat };
            }
            // feature was clicked -> read geojson to get coordinates
            const value = {
                _$layerId: feat.layerId,
                ...feat.geojson.features[0].properties
            };
            const coordinates = new GeoJSON().readFeatures(feat.geojson).map(f => f.getGeometry().getFirstCoordinate());
            if (coordinates.length) {
                value._$coord = coordinates[0];
            }
            return value;
        })
        // attach file links when available
        .forEach(feat => addFilesForFeature(feat, receiveResult));
};

function addFilesForFeature (feature, callback) {
    const layerId = feature._$layerId;
    const attachmentFieldName = LayerHelper.getAttachmentIdFieldName(layerId);
    if (!attachmentFieldName) {
        // not found
        callback(feature);
        return;
    }
    const featureId = feature[attachmentFieldName];

    FileService.listFilesForFeature(layerId, featureId, (files) => {
        if (!files.length) {
            // no attachments
            callback(feature);
            return;
        }
        feature._$files = files;
        callback(feature);
    });
};
