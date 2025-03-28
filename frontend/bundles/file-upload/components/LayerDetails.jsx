import React from 'react';
import PropTypes from 'prop-types';

export const LayerDetails = props => {
    return (
        <div>
            <h3>{`Taso: ${props.name}` || 'Virhe: Tasoa ei valittu'}</h3>
            <label>
        Ominaisuustieto jolla tiedostot liitetään:
                <input
                    type="text"
                    onChange={e => props.onPropertyChange(e.target.value)}
                    onBlur={e => props.onSave(e.target.value)}
                    value={props.attr || ''}
                />
            </label>
        </div>
    );
};

LayerDetails.propTypes = {
    name: PropTypes.string,
    attr: PropTypes.string,
    onPropertyChange: PropTypes.func,
    onSave: PropTypes.func
};
