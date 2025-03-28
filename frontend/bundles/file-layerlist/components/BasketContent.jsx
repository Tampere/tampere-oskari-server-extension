import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { Tooltip } from 'antd';
import { DeleteTwoTone } from '@ant-design/icons';
import { Message } from 'oskari-ui';
import { LayerHelper } from '../../file-upload/service/LayerHelper';
import { getLayerFromService } from '../helpers/layerHelper';
import { getFileLinksForFeature } from './Buttons';
import { Basket } from '../basket';

export const BasketContent = ({ contents = [], onRemove }) => {
    const layers = groupByLayer(contents);
    return (
        <React.Fragment>
            { Object.keys(layers).map(layerId => <LayerItems key={layerId} layer={layers[layerId]} onRemove={onRemove} />) }
        </React.Fragment>
    );
};
BasketContent.propTypes = {
    contents: PropTypes.any,
    onRemove: PropTypes.func.isRequired
};

const LayerItems = ({ layer, onRemove }) => {
    return (
        <React.Fragment>
            <LayerName>
                { layer.name }
                { layer.features.length > 1 &&
                    <RemoveLayerSelections layerId={layer.id} />
                }
            </LayerName>
            <ul>
                { layer.features.map(item => (
                    <Feature key={item[layer.idField]} item={item} layer={layer} onRemove={onRemove} />
                )) }
            </ul>
        </React.Fragment>
    );
};
LayerItems.propTypes = {
    layer: PropTypes.any.isRequired,
    onRemove: PropTypes.func.isRequired
};
const LayerName = styled('h4')`
    padding-top: 20px;
    padding-bottom: 5px;
`;

const RemoveLayerSelections = ({ layerId }) => (
    <IconContainer>
        <Tooltip title={<Message messageKey='buttons.removeLayerFromBasket' />} placement="left">
            <span><RemoveFromLayerIcon layer={layerId} /></span>
        </Tooltip>
    </IconContainer>);
RemoveLayerSelections.propTypes = {
    layerId: PropTypes.any.isRequired
};

const Feature = ({ item, layer, onRemove }) => {
    const idField = layer.idField;
    return (<StyledItem>
        <b><Tooltip title={idField}><span dangerouslySetInnerHTML={{ __html: item[idField] }} /></Tooltip></b>
        <RemoveIcon item={item} onRemove={onRemove} />
        <br/>
        {getFileLinksForFeature(layer.id, item._$files)}
    </StyledItem>);
};
Feature.propTypes = {
    item: PropTypes.any.isRequired,
    layer: PropTypes.any.isRequired,
    onRemove: PropTypes.func.isRequired
};
const StyledItem = styled('li')`
    list-style-type: none;
    border: 1px dashed rgb(200, 200, 200, 0.3);
    padding: 5px;
    margin-bottom: 5px;
`;

export const RemoveAllIcon = () => {
    return (<DeleteTwoTone twoToneColor="#FF0000" onClick={() => Basket.clear()}/>);
};
const RemoveFromLayerIcon = ({ layer }) => {
    return (<DeleteTwoTone twoToneColor="#FF0000" onClick={() => Basket.clear(layer)}/>);
};
RemoveFromLayerIcon.propTypes = {
    layer: PropTypes.any.isRequired
};
const IconContainer = styled('div')`
    float: right;
`;
const RemoveIcon = ({ item, onRemove }) => {
    return (<IconContainer><DeleteTwoTone twoToneColor="#FF0000" onClick={() => onRemove(item)}/></IconContainer>);
};
RemoveIcon.propTypes = {
    item: PropTypes.any.isRequired,
    onRemove: PropTypes.func.isRequired
};

// (contents: object with keys like {_$layerId, DIAARINRO, DOKUMENTIT, _oid, _$coord, _$files}).
const groupByLayer = (contents) => {
    const value = {};
    contents.forEach(item => {
        const layer = item._$layerId;
        if (!value[layer]) {
            const attachmentFieldName = LayerHelper.getAttachmentIdFieldName(layer);
            const layerItem = getLayerFromService(layer);
            value[layer] = {
                id: layer,
                idField: attachmentFieldName,
                name: layerItem.getName(),
                features: []
            };
        }
        value[layer].features.push(item);
    });
    return value;
};
