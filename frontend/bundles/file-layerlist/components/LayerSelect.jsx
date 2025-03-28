import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

const StyledRootEl = styled('div')`
    margin: 5px;
    padding: 10px;
    border: 1px solid ${props => props.selected ? '#096DD9' : '#1890FF'};
    border-radius: 5px;
    background: ${props => props.selected ? '#096DD9' : '#40A9FF'};
    color: white;
`;
// used for toggling layers
let lastSelectedLayer;
function handleClick (layer, isSelected) {
    if (isSelected) {
        Oskari.getSandbox().postRequestByName('RemoveMapLayerRequest', [layer.getId()]);
        lastSelectedLayer = null;
    } else {
        if (lastSelectedLayer) {
            Oskari.getSandbox().postRequestByName('RemoveMapLayerRequest', [lastSelectedLayer]);
        }
        Oskari.getSandbox().postRequestByName('AddMapLayerRequest', [layer.getId()]);
        lastSelectedLayer = layer.getId();
    }
}

export const LayerSelect = ({ layer, isSelected = false }) => {
    return (
        <StyledRootEl selected={isSelected} onClick={() => handleClick(layer, isSelected)}>
            { layer.getName() }
        </StyledRootEl>
    );
};

LayerSelect.propTypes = {
    layer: PropTypes.object.isRequired,
    isSelected: PropTypes.bool
};
