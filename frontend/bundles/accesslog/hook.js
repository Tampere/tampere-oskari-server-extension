export const addAccessLogNotice = () => {
    const originalHandler = Oskari.getSandbox().requestHandler('AddMapLayerRequest');
    Oskari.getSandbox().requestHandler('AddMapLayerRequest', {
        handleRequest: (unused, request) => {
            var sandbox = Oskari.getSandbox();
            var mapLayerService = sandbox.getService('Oskari.mapframework.service.MapLayerService');
            var layer = mapLayerService.findMapLayer(request.getMapLayerId());
            if (layer.getAttributes().sensitive !== true) {
                // pass through as normal
                originalHandler.handleRequest.apply(originalHandler, [unused, request]);
                return;
            }
            // notify that we are tracking usage
            showConfirm(accepted => {
                if (accepted) {
                    originalHandler.handleRequest.apply(originalHandler, [unused, request]);
                } else {
                    // trigger update on layerlist (so layer that is not added is not shown as added)   
                    var evt = Oskari.eventBuilder('AfterMapLayerRemoveEvent')(layer);
                    sandbox.notifyAll(evt);
                }
            });
        }
    });
    const showConfirm = (done) => {
        const dialog = Oskari.clazz.create('Oskari.userinterface.component.Popup');
        dialog.makeModal();
        var cancelBtn = Oskari.clazz.create('Oskari.userinterface.component.buttons.CancelButton');
        var okBtn = Oskari.clazz.create('Oskari.userinterface.component.buttons.OkButton');
        cancelBtn.setHandler(() => { dialog.close(); done(false); });
        okBtn.setHandler(() => { dialog.close(); done(true); });

        const title = Oskari.getMsg('AccessLog', 'title');
        const content = Oskari.getMsg('AccessLog', 'warning');
        dialog.show(title, content, [cancelBtn, okBtn]);
    };
};
