import React, { useState } from 'react';
import { Drawer, Button, Badge, Tooltip } from 'antd';
import 'antd/es/button/style/index.js';
import 'antd/es/drawer/style/index.js';
import 'antd/es/tooltip/style/index.js';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { Message } from 'oskari-ui';
import { LayerSelect } from './LayerSelect';
import { BasketContent, RemoveAllIcon } from './BasketContent';
import { Basket } from '../basket';

const StyledRootEl = styled('div')`
    position: fixed;
    top: 10px;
    left: 10px;
    z-index: 50;
    > button {
        margin-right: 10px;
    }
`;

export const MainPanel = ({ layers = [], selectedLayers = [], drawControl, isDrawing }) => {
    const [layerSelectVisible, showLayerSelect] = useState(true);
    const [basketVisible, showBasket] = useState(false);
    const isSelected = (layer) => {
        return selectedLayers.some(l => l.getId() === layer.getId());
    };
    const hasLayers = layers.length > 0;
    const fileLayerSelected = layers.some(isSelected);
    const hasSelections = Basket.list().length > 0;
    if (basketVisible && !hasSelections) {
        // in case remove all was clicked on basket
        showBasket(false);
    }
    return (
        <React.Fragment>
            <StyledRootEl>
                <Button type="primary" onClick={() => showLayerSelect(!layerSelectVisible)}>
                    <Message messageKey='buttons.layerSelection' />
                </Button>
                <Button onClick={drawControl} disabled={!fileLayerSelected}>
                    { isDrawing ? <Message messageKey='buttons.endDraw' /> : <Message messageKey='buttons.drawSelection' /> }
                </Button>
                <Badge count={Basket.list().length}>
                    <Button onClick={() => showBasket(!basketVisible)} disabled={!hasSelections}>
                        <Message messageKey='buttons.basket' />
                    </Button>
                </Badge>
            </StyledRootEl>
            <Drawer
                title={<Message messageKey='layerSelection.title' />}
                placement={'left'}
                closable={true}
                onClose={() => showLayerSelect(false)}
                visible={layerSelectVisible}
            >
                { !hasLayers &&
                <b><Message messageKey='noLayersWithFiles' /></b>
                }

                { layers.map(layer =>
                    <LayerSelect
                        key={layer.getId()}
                        layer={layer}
                        isSelected={isSelected(layer)} />) }

                <AdditionalDatasetInfo />
            </Drawer>
            <Drawer
                title={<BasketTitle />}
                placement={'right'}
                closable={true}
                onClose={() => showBasket(false)}
                width={350}
                bodyStyle={{
                    paddingTop: 0
                }}
                visible={basketVisible} >
                <BasketContent
                    contents={Basket.list()}
                    onRemove={(item) => Basket.remove(item) }/>
            </Drawer>
        </React.Fragment>
    );
};

const BasketTitle = () => (
    <React.Fragment>
        <Message messageKey='basket.title' /> <Tooltip placement="bottom" title={<Message messageKey='buttons.clearBasket' />}><span><RemoveAllIcon /></span></Tooltip>
        <BasketSubTitle>
            <Message messageKey='basket.byDownloadingYouAcceptTermsOfUse' />
        </BasketSubTitle>
    </React.Fragment>
);

const BasketSubTitle = styled('div')`
    margin-top: 5px;
    font-size: 80%;
`;

const StyledAdditionalMetadata = styled('div')`
    margin: 5px;
    padding: 10px;
    border: 1px solid #1890FF;
    border-radius: 5px;
`;

const AdditionalDatasetInfo = () => (
    <StyledAdditionalMetadata>
        <Message messageKey='additionalDatasetCanBeOrdered' allowHTML='true' />        
    </StyledAdditionalMetadata>
);

MainPanel.propTypes = {
    layers: PropTypes.any,
    selectedLayers: PropTypes.any,
    drawControl: PropTypes.func.isRequired,
    isDrawing: PropTypes.bool
};
