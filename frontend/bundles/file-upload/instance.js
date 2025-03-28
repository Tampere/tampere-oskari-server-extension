import React from 'react';
import ReactDOM from 'react-dom';
import { FileUploadPanel } from './components/FileUploadPanel';
import { LayerDetails } from './components/LayerDetails';
import { ProgressBar } from './components/ProgressBar';
import { FileListing } from './components/FileListing';
import { FileService } from './service/FileService';
import { LayerHelper } from './service/LayerHelper';

const BasicBundle = Oskari.clazz.get('Oskari.BasicBundle');
const flyout = Oskari.clazz.create(
    'Oskari.userinterface.extension.ExtraFlyout',
    'Lisää tiedostoja'
);
flyout.move(170, 0, true);
flyout.makeDraggable();
const mainUI = jQuery('<div></div>');
flyout.setContent(mainUI);

let layer = {};

Oskari.clazz.defineES(
    'Oskari.file-upload.BundleInstance',
    class FileUploadBundle extends BasicBundle {
        constructor () {
            super();
            this.__name = 'file-upload';
            this.eventHandlers = {
                MapLayerEvent: (event) => {
                    if (event.getOperation() !== 'add') {
                        // only handle add layer
                        return;
                    }
                    if (event.getLayerId()) {
                        LayerHelper.addLayerTool(event.getLayerId(), showFlyout);
                    } else { // initial layer load
                        LayerHelper.setupLayerTools(showFlyout);
                    }
                },
                GetInfoResultEvent: (event) => {
                    addFileListing(event);
                }
            };
        }

        _startImpl () {
            LayerHelper.setupLayerTools(showFlyout);
        }
    }
);

let eventDetection = [];

function addFileListing (gfiResultEvent) {
    // Hacky way of detecting if we sent this...
    const filtered = eventDetection.filter(e => e !== gfiResultEvent);
    if (filtered.length !== eventDetection.length) {
        // remove from detection
        eventDetection = filtered;
        // Don't react again
        return;
    }
    // Nope, all good, not going to infinity and beyond!
    const { layerId, features, lonlat } = gfiResultEvent.getData();
    const featureId = LayerHelper.getAttachmentFeatureId(layerId, features);
    if (!featureId) {
        // not found
        return;
    }

    FileService.listFilesForFeature(layerId, featureId, (files) => {
        if (!files.length) {
            // no attachments
            return;
        }
        const infoEvent = Oskari.eventBuilder('GetInfoResultEvent')({
            layerId,
            features: [{
                layerId,
                presentationType: 'Hack to inject more HTML on GFI popup!',
                content: FileService.getFileLinksForFeature(layerId, files)
            }],
            lonlat,
            // just to force "WMS" style parsing
            via: 'ajax'
        });
        eventDetection.push(infoEvent);
        Oskari.getSandbox().notifyAll(infoEvent);
    });
}

function showFlyout (layerId) {
    const maplayer = LayerHelper.getLayerService().findMapLayer(layerId);
    layer = {
        id: maplayer.getId(),
        name: maplayer.getName(),
        attr: maplayer.getAttributes(LayerHelper.KEY_ATTRIBUTES_ATTACHMENT) || 'id'
    };
    FileService.listFilesForLayer(layer.id, function (files) {
        layer = {
            files,
            ...layer
        };
        updateUI(layer);
    });
    flyout.show();
    updateUI(layer);
}

function updateUI (layer, progress) {
    ReactDOM.render(
        <React.Fragment>
            <LayerDetails
                {...layer}
                onPropertyChange={value => changeLayerAttr(value, false)}
                onSave={value => changeLayerAttr(value, true)}
            />
            <FileUploadPanel onSubmit={submitFiles} />
            <ProgressBar value={progress || 0} />
            <FileListing
                files={layer.files || []}
                layerId={layer.id}
                onDelete={removeFile} />
        </React.Fragment>,
        mainUI[0]);
}

function removeFile (layerId, fileId) {
    var r = confirm(`Haluatko poistaa tiedoston?`);
    if (r === true) {
        FileService.removeFile(layerId, fileId, () => {
            // console.log(`Removed from ${layerId} file ${fileId}`);
            // update file listing
            showFlyout(layer.id);
        });
    }
}

function changeLayerAttr (value, callServer) {
    // save to server
    if (callServer) {
        const maplayer = LayerHelper.getLayerService().findMapLayer(layer.id);
        const attrib = maplayer.getAttributes();
        const oldValue = attrib[LayerHelper.KEY_ATTRIBUTES_ATTACHMENT];
        attrib[LayerHelper.KEY_ATTRIBUTES_ATTACHMENT] = value;
        maplayer.setAttributes(attrib);
        var url = Oskari.urls.getRoute('WFSAttachmentsLayer') + `&layerId=${layer.id}&attachmentKey=${value}`;
        jQuery.ajax({
            url: url,
            type: 'PUT',
            // restore old value on error
            error: () => changeLayerAttr(oldValue, false)
        });
    }
    layer.attr = value || 'id';
    updateUI(layer);
}

function submitFiles (data, resetCB = () => {}) {
    FileService.uploadFiles(
        layer.id,
        data.files,
        (progress) => updateUI(layer, progress),
        () => {
            alert('Tiedostot lisätty');
            resetCB();
            // update file listing
            showFlyout(layer.id);
        },
        () => {
            updateUI(layer, 0);
            alert('Virhe tiedostojen siirrossa');
        });
}
