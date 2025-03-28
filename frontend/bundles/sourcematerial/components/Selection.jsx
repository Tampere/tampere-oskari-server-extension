import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { Switch, Message } from 'oskari-ui';
import { addFeaturesToMap, clearFeaturesFromLayer, LAYER_SELECTION } from '../service/featuresHelper';

export const Selection = ({ feature }) => {
    if (!feature) {
        clearFeaturesFromLayer(LAYER_SELECTION);
        return (<Message messageKey='layerSelection.noSelection' />);
    }
    const [isShown, setShown] = useState(true);

    useEffect(() => {
        onChange(isShown);
    });
    const onChange = (checked) => {
        if (checked) {
            addFeaturesToMap(feature, {
                layerId: LAYER_SELECTION
                // centerTo: true
            });
        } else {
            clearFeaturesFromLayer(LAYER_SELECTION);
        }
        setShown(checked);
    };

    return (<div>
        <Switch onChange={onChange} checked={isShown} /> <Message messageKey='layerSelection.showSelection' />
        <br />
    </div>);
};

Selection.propTypes = {
    feature: PropTypes.object
};
