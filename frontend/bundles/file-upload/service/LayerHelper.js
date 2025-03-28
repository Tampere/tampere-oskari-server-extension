
const KEY_ATTRIBUTES_ATTACHMENT = 'attachmentKey';

/**
 * Adds the layer attachments tool for layer
 * @param  {String| Number} layerId layer to process
 * @param  {Function} toolCB function to call on click
 * @param  {Boolean} suppressEvent true to not send event about updated layer (optional)
 */
function addLayerTool (layer, toolCB, suppressEvent) {
    if (!isAdmin()) {
        return;
    }
    const service = getLayerService();
    if (typeof layer !== 'object') {
        // detect layerId and replace with the corresponding layerModel
        layer = service.findMapLayer(layer);
    }
    if (!layer.isLayerOfType('wfs')) {
        // console.log(layer.getType());
        return;
    }
    // add feature data tool for layer
    const tool = Oskari.clazz.create('Oskari.mapframework.domain.Tool');
    tool.setName('layer-attachment');
    // tool.setIconCls('show-file-attachment-tool');
    // TODO: setup some icon for this
    tool.setIconCls('show-layer-legend-tool');
    tool.setTooltip('Attachments');
    tool.setTypes(['selectedLayers']);

    tool.setCallback(() => {
        toolCB(layer.getId());
    });
    service.addToolForLayer(layer, tool, suppressEvent);
}

/**
 * Adds tools for all layers
 * @param  {Function} toolCB function to call on click
 */
function setupLayerTools (toolCB) {
    if (!isAdmin()) {
        return;
    }
    // add tools for feature data layers
    const service = getLayerService();
    const layers = service.getLayersOfType('wfs');
    layers.forEach(layer => {
        addLayerTool(layer, toolCB, true);
    });
    // update all layers at once since we suppressed individual events
    const event = Oskari.eventBuilder('MapLayerEvent')(null, 'tool');
    Oskari.getSandbox().notifyAll(event);
}

function isAdmin () {
    return Oskari.user().getRoles()
        .find(r => r.name.includes('Admin'));
}

function getLayerService () {
    return Oskari.getSandbox()
        .getService('Oskari.mapframework.service.MapLayerService');
}
function getAttachmentIdFieldName (layerId) {
    const maplayer = LayerHelper.getLayerService().findMapLayer(layerId);
    return maplayer.getAttributes(KEY_ATTRIBUTES_ATTACHMENT) || 'id';
}

function getAttachmentFeatureId (layerId, features) {
    if (features.length !== 1) {
        return;
    }
    const featureProps = features[0];
    const featureMappingField = getAttachmentIdFieldName(layerId);
    return featureProps[featureMappingField];
}

export const LayerHelper = {
    addLayerTool,
    setupLayerTools,
    getLayerService,
    getAttachmentFeatureId,
    getAttachmentIdFieldName,
    KEY_ATTRIBUTES_ATTACHMENT
};
