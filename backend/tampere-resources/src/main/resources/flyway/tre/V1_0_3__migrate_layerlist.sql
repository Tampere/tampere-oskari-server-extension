UPDATE oskari_appsetup_bundles
    SET bundle_id = (select id from oskari_bundle where name = 'layerlist'),
        bundleinstance='',
        config='{}',
        state='{}'
    where bundle_id = (select id from oskari_bundle where name = 'hierarchical-layerlist');
