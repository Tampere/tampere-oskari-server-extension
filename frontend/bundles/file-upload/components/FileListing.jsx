import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { DeleteTwoTone } from '@ant-design/icons';
import { Button, Space } from 'oskari-ui';
import { QNAME } from 'oskari-frontend/bundles/mapping/mapmodule/service/VectorFeatureSelectionService';
import { LayerHelper } from '../service/LayerHelper';

export const StyledList = styled('ul')`
    list-style-type: none;
`;

export const StyledListItem = styled('li')`
    padding: 5px;
    border: 1px solid gray;
    border-radius: 3px;
    margin-bottom: 2px;
    align-items: center;

    :hover {
        background-color: #FDF8D9;
    }
`;
const getListing = (files) => {
    const model = {};
    files.forEach(element => {
        let feature = model[element.featureId] || {
            id: element.featureId,
            files: []
        };
        feature.files.push(element);
        model[element.featureId] = feature;
    });
    return Object.values(model);
};
export const FileListing = ({ files = [], layerId, onDelete = () => {} }) => {
    const model = getListing(files);
    return (
        <div>
            <h4>Tiedostot:</h4>
            <StyledList>
                {model.map(f => (
                    <FeatureItem
                        key={f.id + f.files.map(m => m.id).join('_')}
                        layerId={layerId}
                        feature={f}
                        onDelete={onDelete} />
                ))}
                {!model.length && (<li>Ei tiedostoja</li>) }
            </StyledList>
        </div>
    );
};

/*
const onMouseEnter = (layerId, featureValue) => {
    const featureMappingField = LayerHelper.getAttachmentIdFieldName(layerId);
//    TODO:
//    1) find feature with properties[featureMappingField] = featureValue
//    2) call addSelectedFeature(layerId, featureId)

    const selectionService = Oskari.getSandbox().getService(QNAME);
    selectionService.addSelectedFeature(layerId, featureValue);
};

const onMouseOut = (layerId, featureValue) => {
    console.log(layerId, featureValue);
    const selectionService = Oskari.getSandbox().getService(QNAME);
    selectionService.removeSelection(layerId, featureValue);
};
*/
const FeatureItem = ({ feature, layerId, onDelete }) => {
    return (
        <StyledListItem
            // onMouseEnter={() => onMouseEnter(layerId, feature.id)}
            // onMouseLeave={() => onMouseOut(layerId, feature.id)}
        >
            <b>Kohde: {feature.id}</b> / Tiedostot:
            <div>
                <Space>
                    {feature.files.map(f => (
                        <FileItem
                            key={f.id}
                            file={f}
                            onDelete={onDelete} />
                    ))}
                </Space>
            </div>
        </StyledListItem>
    );
};

const FileItem = ({ file, onDelete }) => {
    return (
        <Button danger onClick={() => onDelete(file.layerId, file.id)}>
            <DeleteTwoTone twoToneColor='#FF0000' /> {file.locale}
        </Button>
    );
};

FileListing.propTypes = {
    files: PropTypes.any,
    layerId: PropTypes.any,
    onDelete: PropTypes.func
};
