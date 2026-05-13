import React from 'react';
import { getReactRoot } from 'oskari-ui/components/window';
import Overlay from 'ol/Overlay';
import { LocaleProvider } from 'oskari-ui/util';
import './Popup.css';
import { ObjectData } from './ObjectData';

let overlay;

export const hidePopup = () => {
    overlay && overlay.setPosition(undefined);
    return false;
};

// Create popup DOM imperatively so elements exist synchronously before OL overlay is created
const addMapOverlay = () => {
    const container = document.createElement('div');
    container.id = 'popup';
    container.className = 'ol-popup';

    const closer = document.createElement('a');
    closer.href = '#';
    closer.id = 'popup-closer';
    closer.className = 'ol-popup-closer';
    closer.onclick = hidePopup;

    const content = document.createElement('div');
    content.id = 'popup-content';

    container.appendChild(closer);
    container.appendChild(content);
    document.body.appendChild(container);

    overlay = new Overlay({
        element: container,
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
};
