import React from 'react';
import { Messaging } from 'oskari-ui/util';
import { ObjectData } from './components/ObjectData';

const getFileId = (file) => {
    return file._$layerId + '||' + (file._oid || file.__fid);
};
export const showMessage = (file) => {
    Messaging.open({
        title: 'Ladattava kohde',
        content: (<ObjectData item={file} />)
    });
};

const selectedFiles = {};
const listeners = [];
const notify = () => {
    listeners.forEach(l => l());
};

export const Basket = {
    add: (file) => {
        selectedFiles[getFileId(file)] = file;
        notify();
    },
    isInBasket: (file) => {
        return !!selectedFiles[getFileId(file)];
    },
    list: () => Object.values(selectedFiles),
    remove: (file) => {
        delete selectedFiles[getFileId(file)];
        notify();
    },
    clear: (layer) => {
        const keys = Object.keys(selectedFiles);
        keys
            .filter(key => !layer || key.startsWith(layer + '||'))
            .forEach(key => {
                delete selectedFiles[key];
            });
        notify();
    },
    onChange: (listener) => listeners.push(listener)
};
