import React from 'react';
import PropTypes from 'prop-types';

export const FileDisplay = ({ file, onRemove, onRename }) => {
    return (
        <div>
            <input type="text" value={file.locale || file.name}
                onChange={(event) => onRename(file, event.target.value)}/>
            <button type="button" onClick={() => onRemove(file)}>
        Poista
            </button>
        </div>
    );
};

FileDisplay.propTypes = {
    file: PropTypes.any,
    onRemove: PropTypes.func,
    onRename: PropTypes.func
};
