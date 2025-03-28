import React from 'react';
import ReactDOM from 'react-dom';
import { Message } from 'oskari-ui';
import { Messaging, LocaleProvider } from 'oskari-ui/util';
import { MainPanel } from './components/MainPanel';
import { showPopup, hidePopup } from './components/Popup';
import { Basket } from './basket';
import { processFeatures } from './helpers/featureshelper';
import { getLayers, getLayerFromService } from './helpers/layerHelper';
import { getFeaturesInGeom } from './helpers/geomSelector';
import { createFeatureClickHelper } from '../util/FeatureClickHelper';
import { QNAME as SELECTION_SERVICE_NAME } from 'oskari-frontend/bundles/mapping/mapmodule/service/VectorFeatureSelectionService';

Basket.onChange(updateUI);

const BasicBundle = Oskari.clazz.get('Oskari.BasicBundle');

const FEATURE_SELECT_ID = 'attachmentSelection';
let isDrawing = false;
const startDrawSelection = () => {
    Oskari.getSandbox().postRequestByName('DrawTools.StartDrawingRequest', [FEATURE_SELECT_ID, 'Polygon']);
    isDrawing = true;
    updateUI();
};
const endDrawSelection = () => {
    Oskari.getSandbox().postRequestByName('DrawTools.StopDrawingRequest', [FEATURE_SELECT_ID, true, true]);
    isDrawing = false;
    updateUI();
};
const toggleDrawing = () => {
    isDrawing ? endDrawSelection() : startDrawSelection();
};
const BUNDLE_ID = 'file-layerlist';
class FileLayerListBundle extends BasicBundle {
    constructor () {
        super();
        this.__name = BUNDLE_ID;
        this.loc = Oskari.getMsg.bind(null, this.__name);
        this.clickHelper = createFeatureClickHelper();
        this.clickHelper.addListener((coords, features) => {
            processFeatures(features, (processedFeatures) => {
                showPopup(coords.lon, coords.lat, processedFeatures[0]);
            });
        });
        this.eventHandlers = {
            AfterMapLayerAddEvent: () => {
                this.clickHelper.setLayer(getSelectedWFSLayerId());
                updateUI();
            },
            AfterMapLayerRemoveEvent: () => {
                this.clickHelper.setLayer(getSelectedWFSLayerId());
                updateUI();
            },
            DrawingEvent: (event) => {
                if (!event.getIsFinished() || event.getId() !== FEATURE_SELECT_ID) {
                    // only interested in finished drawings for attachment selection
                    return;
                }
                endDrawSelection();
                const layerId = getSelectedWFSLayerId();
                if (!layerId) {
                    return;
                }
                const features = getFeaturesInGeom(event.getGeoJson().features[0].geometry, layerId);
                const mapped = features.map(f => {
                    const value = {
                        ...f.getProperties(),
                        _$layerId: layerId
                    };
                    delete value.geometry;
                    return value;
                });
                processFeatures(mapped, (features) => {
                    const featuresWithFiles = features.filter(feat => feat._$files && feat._$files.length);
                    featuresWithFiles.forEach(feat => { Basket.add(feat); });
                    if (featuresWithFiles.length) {
                        Messaging.success(<Message messageKey='addedToBasket' bundleKey={BUNDLE_ID} />);
                    } else {
                        Messaging.warn(<Message messageKey='drawNoHits' bundleKey={BUNDLE_ID} />);
                    }
                });
            }
        };
    }

    _startImpl () {
        updateUI();
    }
}

function getSelectedWFSLayerId () {
    const selectedLayers = Oskari.getSandbox().getMap().getLayers().filter(l => l.isLayerOfType('WFS'));
    if (!selectedLayers.length) {
        // No WFS-layers selected
        // TODO: don't allow draw if no layers selected...
        return;
    }
    return selectedLayers[0].getId();
}
function highlightFeatures () {
    const selectionService = Oskari.getSandbox().getService(SELECTION_SERVICE_NAME);
    selectionService.removeSelection();
    const features = Basket.list();
    if (!features.length) {
        return;
    }
    const layer = getLayerFromService(getSelectedWFSLayerId());
    if (!layer) {
        return;
    }
    const featureIds = features
        .filter(f => f._$layerId === layer.getId())
        .map(f => f._oid);
    selectionService.setSelectedFeatureIds(layer.getId(), featureIds);
    // TODO: group by layer etc
}

function updateUI () {
    getLayers((layers) => {
        // hide any possibly open popups on map before re-render (in case layer was removed etc)
        hidePopup();
        // update highlighted features based on basket content
        highlightFeatures();
        const selectedLayers = Oskari.getSandbox().getMap().getLayers();
        ReactDOM.render(
            <LocaleProvider value={{ bundleKey: BUNDLE_ID }}>
                <MainPanel layers={layers} selectedLayers={selectedLayers} drawControl={toggleDrawing} isDrawing={isDrawing}/>
            </LocaleProvider>, getRoot());
    });
}

let root = null;
function getRoot () {
    if (!root) {
        const mapEl = document.getElementById('contentMap');
        root = document.createElement('div');
        mapEl.appendChild(root);
    }
    return root;
}

// so bundle.js can reference this
Oskari.clazz.defineES('Oskari.file-layerlist.BundleInstance', FileLayerListBundle);
