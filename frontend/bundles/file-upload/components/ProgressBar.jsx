import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

function isVisible (max, value) {
    return value !== 0 && value !== max;
}
export const ProgressBar = ({ max = 100, value = 0 }) => {
    const StyledBar = styled('progress')`
    width: 100%;
    visibility: ${isVisible(max, value) ? 'visible' : 'hidden'};
  `;
    return (
        <StyledBar max={max} value={value} visible={value !== 0 && value !== max} />
    );
};

ProgressBar.propTypes = {
    max: PropTypes.number,
    value: PropTypes.number
};
