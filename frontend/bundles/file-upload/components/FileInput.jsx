import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

const StyledRootEl = styled('div')`
  margin: 10px;
  padding: 10px;
  border: 1px dotted black;
`;

const StyledFileInput = styled('input')`
  display: none;
`;

const ButtonLabel = styled('label')`
  display: block;
  text-align: center;
  border: 1px solid black;
  cursor: pointer;
  border-radius: 5px;
  padding: 10px;
  margin: 10px;
  background-color: orange;
`;

export const FileInput = ({ onFiles }) => {
    const onDrop = event => {
        event.preventDefault();
        onFiles(event.dataTransfer.files);
    };
    return (
        <StyledRootEl
            className="dropzone"
            onDragEnter={onDragEnter}
            onDragOver={onDragOver}
            onDrop={onDrop}
            onDragLeave={onDragLeave}
        >
            <ButtonLabel>
                <StyledFileInput
                    type="file"
                    multiple
                    onChange={e => onFiles(e.target.files)}
                />
        Valitse tiedostoja tai pudota ne tähän
            </ButtonLabel>
        </StyledRootEl>
    );
};

FileInput.propTypes = {
    onFiles: PropTypes.func
};

/* -------------------------
 * Just for debugging
 */
function onDragOver (event) {
    event.preventDefault();
    // console.log("Drag over", event);
}
function onDragLeave (event) {
    event.preventDefault();
    // console.log("Drag leave", event);
}
function onDragEnter (event) {
    event.preventDefault();
    // console.log("Drag enter", event);
}
