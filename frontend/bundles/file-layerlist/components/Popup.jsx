import React from 'react';
import { getReactRoot } from 'oskari-ui/components/window';
import Overlay from 'ol/Overlay';
import { LocaleProvider } from 'oskari-ui/util';
import './Popup.css';
import { ObjectData } from './ObjectData';

let overlay;

export const hidePopup = () => {
    overlay && overlay.setPosition(undefined);
    const closer = document.getElementById('popup-closer');
    closer && closer.blur();
    return false;
};

// hacky way to force popup rendered with React on OpenLayers map
const addMapOverlay = () => {
    const wrapper = document.createElement('div');
    document.body.appendChild(wrapper);
    getReactRoot(wrapper).render(<div id="popup" className="ol-popup">
        <a href="#" id="popup-closer" className="ol-popup-closer"></a>
        <div id="popup-content"></div>
    </div>);

    overlay = new Overlay({
        element: document.getElementById('popup'),
        autoPan: true,
        autoPanAnimation: {
            duration: 250
        }
    });

    const olMap = Oskari.getSandbox().findRegisteredModuleInstance('MainMapModule').getMap();
    olMap.addOverlay(overlay);
};

export const showPopup = (x, y, content) => {
    if (!overlay) {
        addMapOverlay();
    }
    const el = document.getElementById('popup-content');
    getReactRoot(el).render(<LocaleProvider value={{ bundleKey: 'file-layerlist' }}><ObjectData item={content} addBasketLink={true} /></LocaleProvider>);
    overlay.setPosition([x, y]);
    const closeBtn = document.getElementById('popup-closer');
    if (!closeBtn.onclick) {
        closeBtn.onclick = hidePopup;
    }
};
