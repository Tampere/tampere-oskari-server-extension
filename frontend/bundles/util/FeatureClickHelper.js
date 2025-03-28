/**
 * Creates a helper that listens to Oskari map clicks and feature clicks and matches the two to notify listeners
 * with coordinates that were clicked with features that are in that location on the layer that we are interested in.
 * If you want to open a custom popup on clicked map location with content about the features that were hit, this makes it easier for you.
 *
 * Usage:
 *
        const helper = createFeatureClickHelper();
        helper.addListener((coords, features) => {
            // do stuff -> user clicked the map at coords and there are features referenced in the second param in that location
        });

        Note! The click handler only cares about features on one layer. You need to set the layer that you are interested in by listening to layer changes on the map and calling:

                helper.setLayer("[layer id]");

 * @param {Oskari.sandbox} sandbox for listening to events. Defaults to Oskari.getSandbox()
 * @returns helper for detecting click location and vector features on that location
 */
export const createFeatureClickHelper = (sandbox = Oskari.getSandbox()) => {
    const NAME = 'FeatureClickHelper_' + Oskari.getSeq('clickHelper').nextVal();
    let layerOfInterest;
    const listeners = [];

    const clickedFeatures = {
        waiting: false
    };
    const handleFeaturesClicked = (delayed = false) => {
        const { coords, features, waiting } = clickedFeatures;
        if (!coords || !features) {
            // we don't yet have one of the two things we need
            return;
        }
        if (waiting && !delayed) {
            // another call scheduled as a delayed call. Wait for it instead.
            return;
        }
        if (!delayed) {
            // make sure we don't have a saved click from another location if we get
            // the features first. Call itself again with a flag to identify a delayed call.
            clickedFeatures.waiting = true;
            setTimeout(() => handleFeaturesClicked(true), 50);
            return;
        }
        // reset waiting if we get this far
        // -> called with delay so we can start expecting more delayed calls
        clickedFeatures.waiting = false;
        listeners.forEach(fn => {
            fn(coords, features);
        });
        delete clickedFeatures.coords;
        delete clickedFeatures.features;
    };
 
    const eventHandlers = {
        MapClickedEvent: (event) => {
            clickedFeatures.coords = event.getLonLat();
            handleFeaturesClicked();
        },
        FeatureEvent: (event) => {
            if (event.getOperation() !== 'click' || typeof layerOfInterest === 'undefined') {
                return;
            }
            const interestedInAllLayers = layerOfInterest === null;
            const features = event.getParams().features.filter(f => interestedInAllLayers || f.layerId === layerOfInterest);
            if (!features.length) {
                delete clickedFeatures.coords;
                return;
            }
            clickedFeatures.features = features;
            handleFeaturesClicked();
        }
    };
    const listenerModule = {
        getName: () => NAME,
        onEvent: (event) => {
            var handler = eventHandlers[event.getName()];
            if (!handler) {
                return;
            }
            return handler.call(this, event);
        }
    };

    Object.keys(eventHandlers).forEach((eventName) => sandbox.registerForEventByName(listenerModule, eventName));

    return {
        setLayer: (id) => (layerOfInterest = id),
        addListener: (fn) => listeners.push(fn),
        destroy: () => {
            Object.keys(eventHandlers)
                .forEach((eventName) => sandbox.unregisterFromEventByName(this, eventName));
        }
    };
};
