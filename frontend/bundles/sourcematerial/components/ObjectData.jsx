import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
/**
 * Creates an object data presentation of feature with possible file links
 */
const IGNORED_KEYS = ['_$layerId', '_oid', '__fid', '_$files', '_$coord'];
export const ObjectData = ({ item }) => {
    const shownKeys = Object.keys(item)
        .filter(key => !IGNORED_KEYS.includes(key))
        .map(key => (<Row key={key} field={key} value={item[key]} />));
    return (<Table>
        {shownKeys}
    </Table>);
};
ObjectData.propTypes = {
    item: PropTypes.any.isRequired
};

const StyledTable = styled('table')`
    margin: 0px !important;
    border-top-width: 0px !important;
`;
const Table = ({ children }) => (
    <StyledTable>
        <tbody>{ children }</tbody>
    </StyledTable>);
Table.propTypes = {
    children: PropTypes.any.isRequired
};

const Row = ({ field, value }) => (
    <tr>
        <td>{ field }</td>
        <td dangerouslySetInnerHTML={ { __html: value } } />
    </tr>);
Row.propTypes = {
    field: PropTypes.any.isRequired,
    value: PropTypes.any.isRequired
};
