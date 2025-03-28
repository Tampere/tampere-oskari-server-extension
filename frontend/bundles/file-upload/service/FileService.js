const noopFunc = () => {};

function uploadFiles (layerId, files, progressCB = noopFunc, successCB = noopFunc, errorCB = noopFunc) {
    progressCB(0);
    var url = Oskari.urls.getRoute('WFSAttachments');
    var xhr = new XMLHttpRequest();
    var formData = new FormData();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('X-XSRF-TOKEN', Oskari.app.getXSRFToken());

    // Add following event listener
    xhr.upload.addEventListener('progress', function (e) {
        progressCB((e.loaded * 100.0) / e.total || 100);
    });

    xhr.addEventListener('readystatechange', function (e) {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Done. Inform the user
            successCB();
        } else if (xhr.readyState === 4 && xhr.status !== 200) {
            // Error. Inform the user
            errorCB();
        }
    });

    formData.append('layerId', layerId);
    files.forEach(f => {
        formData.append('file', f);
        if (f.locale) {
            formData.append('locale_' + f.name, f.locale);
        }
    });
    xhr.send(formData);
}

function listLayersWithFiles (successCB, layerJSON = false) {
    var url = Oskari.urls.getRoute('WFSAttachments', { json: layerJSON });
    jQuery.get(url, successCB);
}

function listFilesForLayer (layerId, successCB) {
    var url = Oskari.urls.getRoute('WFSAttachments') +
        `&layerId=${layerId}`;
    jQuery.get(url + `&layerId=${layerId}`, successCB);
}

function listFilesForFeature (layerId, featureId, successCB) {
    var url = Oskari.urls.getRoute('WFSAttachments') +
        `&layerId=${layerId}&featureId=${featureId}`;
    jQuery.get(url, successCB);
}

function openFile (layerId, fileId) {
    var url = Oskari.urls.getRoute('WFSAttachments') +
        `&layerId=${layerId}&fileId=${fileId}`;
    window.open(url, '_blank');
}

function removeFile (layerId, fileId, successCB) {
    var url = Oskari.urls.getRoute('WFSAttachments') +
        `&layerId=${layerId}&fileId=${fileId}`;
    jQuery.ajax({
        url,
        type: 'DELETE',
        success: successCB
    });
}

function getFileLinksForFeature (layerId, files) {
    var url = Oskari.urls.getRoute('WFSAttachments') + `&layerId=${layerId}`;
    const html = files.map(f => {
        let fileLink = `&fileId=${f.id}`;
        if (f.external) {
            const fileName = encodeURIComponent(f.locale) + '.' + f.fileExtension;
            fileLink = `&featureId=${f.featureId}&name=${fileName}`;
        }
        return `<a class="button" target="_blank" 
            rel="noopener noreferer" href="${url + fileLink}">${f.locale}</a>`;
    });
    return `<div>
        <b>Tiedostot:</b> ${html.join(' ')}
    </div>`;
}

export const FileService = {
    uploadFiles,
    listLayersWithFiles,
    listFilesForLayer,
    listFilesForFeature,
    openFile,
    removeFile,
    getFileLinksForFeature
};
