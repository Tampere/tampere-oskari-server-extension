import React from 'react';
import PropTypes from 'prop-types';
import { ObjectData } from './ObjectData';
/**
 * Creates content for source material popup
 */
/*
features: [{
    geojson: {type: "FeatureCollection", features: Array(1)}
    layerId: "SourceMaterialFeatures_2023"
}]
*/
export const PopupContent = ({ features }) => {
    if (!features || !features.length) {
        return (<div>Ei kohdetietoja</div>);
    }
    return (<div>
        { features.map((feat, index) => (
            <React.Fragment key={feat.layerId + '_' + index}>
                <h4>{feat.layerName}</h4>
                { feat.geojson.features.map(item =>
                    (<ObjectData key={item.id} item={item.properties} />)) }
            </React.Fragment>
        ))}
    </div>);
    /*
    const shownKeys = Object.keys(item)
        .filter(key => !IGNORED_KEYS.includes(key))
        .map(key => (<Row key={key} field={key} value={item[key]} />));
    return (<Table>
        {shownKeys}
    </Table>);
    */
};
PopupContent.propTypes = {
    features: PropTypes.any.isRequired
};
