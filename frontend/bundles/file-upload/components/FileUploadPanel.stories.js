import React from 'react';
import { storiesOf } from '@storybook/react';
import { FileUploadPanel } from './FileUploadPanel';

const layer = {
    id: 1,
    name: 'Tason nimi'
};
function onSubmit (data) {
    alert(
        `Attr: ${data.layerAttribute} \nTiedostot: ${data.files
            .map(f => f.name)
            .join(', ')}`
    );
}
storiesOf('FileUploadPanel', module).add('basic', () => (
    <FileUploadPanel layer={layer} onSubmit={onSubmit} />
));
