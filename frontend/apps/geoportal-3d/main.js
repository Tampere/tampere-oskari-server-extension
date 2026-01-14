import 'oskari-bundle!../../bundles/lang-overrides';


// These framework bundles have to be imported first
import 'oskari-bundle!oskari-frontend/bundles/framework/mapfull';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/oskariui/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/divmanazer/bundle.js';

// Then import mapmodule and rest of the application - 3D mapmodule
import 'oskari-bundle!oskari-frontend/bundles/mapping/mapmodule/map3d_olcs';
import 'oskari-bundle!oskari-frontend/bundles/mapping/tiles3d';



import 'oskari-bundle!oskari-frontend/bundles/mapping/mapmyplaces';
import 'oskari-bundle!oskari-frontend/bundles/framework/myplacesimport/mapuserlayers';
import 'oskari-bundle!oskari-frontend/bundles/mapping/maparcgis';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/ui-components/bundle.js';
import 'oskari-bundle!oskari-frontend/bundles/mapping/userstyle';

import 'oskari-bundle!oskari-frontend/bundles/mapping/drawtools';
import 'oskari-bundle!oskari-frontend/bundles/mapping/toolbar';
import 'oskari-bundle!oskari-frontend/bundles/framework/statehandler';
import 'oskari-bundle!oskari-frontend/bundles/mapping/infobox';

import 'oskari-bundle!oskari-frontend/bundles/framework/search';
import 'oskari-bundle!oskari-frontend/bundles/framework/layerlist';
import 'oskari-bundle!oskari-frontend/bundles/framework/mydata';
import 'oskari-bundle!oskari-frontend/bundles/framework/maplegend';

import 'oskari-bundle!oskari-frontend/bundles/framework/coordinatetool';
import 'oskari-bundle!oskari-frontend/bundles/catalogue/metadatasearch';
import 'oskari-bundle!oskari-frontend/bundles/catalogue/metadataflyout';
import 'oskari-bundle!oskari-frontend/bundles/framework/featuredata';

import 'oskari-bundle!oskari-frontend/bundles/framework/myplaces3';

// additional 3d bundles
import 'oskari-bundle!oskari-frontend/bundles/mapping/time-control-3d';
import 'oskari-bundle!oskari-frontend/bundles/mapping/camera-controls-3d';
import 'oskari-bundle!oskari-frontend/bundles/mapping/dimension-change';

// tampereeeeee
import 'oskari-bundle!oskari-frontend-contrib/bundles/tampere/search-from-channels';
// /tampereeeeee

import 'oskari-lazy-bundle?admin-layereditor!oskari-frontend/bundles/admin/admin-layereditor';
import 'oskari-lazy-bundle?admin-announcements!oskari-frontend/bundles/admin/admin-announcements';
import 'oskari-lazy-bundle?admin-permissions!oskari-frontend/bundles/admin/admin-permissions';
import 'oskari-lazy-bundle?admin-users!oskari-frontend/bundles/admin/admin-users';
import 'oskari-lazy-bundle?admin!oskari-frontend/bundles/admin/admin';
import 'oskari-lazy-bundle?admin-wfs-search-channel!oskari-frontend-contrib/bundles/tampere/admin-wfs-search-channel';

import '../../bundles/hacks.js';
import './css/overwritten.css';

import { addAccessLogNotice } from '../../bundles/accesslog/hook.js';

Oskari.on('app.start', () => {
    addAccessLogNotice();
});
