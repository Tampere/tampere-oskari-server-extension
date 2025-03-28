import React from 'react';
import PropTypes from 'prop-types';
import { Basket } from '../basket';
import styled from 'styled-components';
import { Button } from 'antd';
import { Message } from 'oskari-ui';
import { CloudDownloadOutlined, FileAddTwoTone, DeleteTwoTone } from '@ant-design/icons';

const LinkButton = styled(Button)`
    margin-right: 5px;
`;

export const FileLink = ({ link, children }) => {
    return (<LinkButton size="small" onClick={() => {
        window.open(link, '_blank');
        return false;
    }}>
        <CloudDownloadOutlined style={{ color: '#096DD9' }} />{ children }
    </LinkButton>);
};
FileLink.propTypes = {
    link: PropTypes.string.isRequired,
    children: PropTypes.string.isRequired
};

export const BasketLink = ({ item }) => {
    const isInBasket = Basket.isInBasket(item);
    const btnClick = () => {
        if (isInBasket) {
            Basket.remove(item);
        } else {
            Basket.add(item);
        }
        return false;
    };
    let label = 'Poimi koriin';
    if (isInBasket) {
        label = 'Poista korista';
    }
    return (<LinkButton size="small" onClick={btnClick}>
        {!isInBasket && <FileAddTwoTone twoToneColor='#00BB00' /> }
        {isInBasket && <DeleteTwoTone twoToneColor='#FF0000' /> }
        {label}
    </LinkButton>);
};

BasketLink.propTypes = {
    item: PropTypes.any.isRequired
};

// {"external":false,"layerId":2276,"fileExtension":"pdf","id":4,"locale":"TRE 1905198","featureId":"TRE 1905198"}
export const getFileLinksForFeature = (layerId, files = [], addBasketLink, item) => {
    var url = Oskari.urls.getRoute('WFSAttachments') + `&layerId=${layerId}`;
    const fileLinks = files.map(f => {
        let fileLink = `${url}&fileId=${f.id}`;
        if (f.external) {
            const fileName = encodeURIComponent(f.locale) + '.' + f.fileExtension;
            fileLink = `${url}&featureId=${f.featureId}&name=${fileName}`;
        }
        return (<FileLink key={fileLink} link={fileLink}>{f.locale}</FileLink>);
    });

    if (addBasketLink) {
        if (fileLinks.length) {
            fileLinks.push(<BasketLink key='basket' item={item} />);
        } else {
            fileLinks.push(<Message messageKey='noFilesOnFeature' />);
        }
    }
    return fileLinks;
};
