import React from 'react';
import ReactDOM from 'react-dom';
import { Messaging, LocaleProvider } from 'oskari-ui/util';
import { MainPanel } from './components/MainPanel';
import { getService } from './service/layerservice';
import { LAYER_SELECTION } from './service/featuresHelper';
import { showPopup } from './components/Popup';
import { PopupContent } from './components/PopupContent';
import { createFeatureClickHelper } from '../util/FeatureClickHelper';

const BasicBundle = Oskari.clazz.get('Oskari.BasicBundle');

const SOURCEMATERIAL_ID = 'sourcematerial';
let isDrawing = false;
let currentSelection;
const startDrawSelection = () => {
    currentSelection = null;
    Oskari.getSandbox().postRequestByName('DrawTools.StartDrawingRequest', [SOURCEMATERIAL_ID, 'Polygon']);
    isDrawing = true;
    getService(service => service.setSelection(null));
    updateUI();
}; 
const endDrawSelection = () => {
    Oskari.getSandbox().postRequestByName('DrawTools.StopDrawingRequest', [SOURCEMATERIAL_ID, true, true]);
    isDrawing = false;
    updateUI();
};
const toggleDrawing = () => {
    isDrawing ? endDrawSelection() : startDrawSelection();
};

const loc = Oskari.getMsg.bind(null, SOURCEMATERIAL_ID);
class SourceMaterialBundle extends BasicBundle {
    constructor () {
        super();
        this.__name = SOURCEMATERIAL_ID;
        this.loc = loc;
        const helper = createFeatureClickHelper();
        helper.setLayer(null); // null to signify "interest in all layers"
        helper.addListener((coords, features) => {
            getService(service => {
                const layers = service.getLayers();
                const getLayerName = (layerId) => {
                    const layer = layers.find(l => layerId.indexOf(l.id) > 10) || {};
                    return layer.name || 'N/A';
                };
                const content = features.map(layerFeatures => {
                    if (LAYER_SELECTION === layerFeatures.layerId) {
                        return null;
                    }
                    return {
                        ...layerFeatures,
                        layerName: Oskari.getLocalized(getLayerName(layerFeatures.layerId))
                    };
                }).filter(i => !!i);
                showPopup(coords.lon, coords.lat, <PopupContent features={content} />);
            });
        });
        this.eventHandlers = {
            DrawingEvent: (event) => {
                if (!event.getIsFinished() || event.getId() !== SOURCEMATERIAL_ID) {
                    // only interested in finished drawings for source material selection
                    return;
                }
                currentSelection = event.getGeoJson().features[0];
                getService(service => service.setSelection(currentSelection));
                endDrawSelection();
            }
        };
    }

    _startImpl () {
        // init service and call update UI that will get the same ref
        getService((service) => {
            service.addListener(() => updateUI(service));
            updateUI();
        });
    }
}

function updateUI () {
    getService((service) => {
        const hasAccess = service.getRoles().length > 0;
        if (!hasAccess) {
            Messaging.error(loc('accessdenied'));
            return;
        }
        const state = service.getState();
        ReactDOM.render(
            <LocaleProvider value={{ bundleKey: SOURCEMATERIAL_ID }}>
                <MainPanel service={service}
                    drawControl={toggleDrawing}
                    isDrawing={isDrawing}
                    state={state} />
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
Oskari.clazz.defineES('Oskari.sourcematerial.BundleInstance', SourceMaterialBundle);
