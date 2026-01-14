import 'oskari-bundle!../../bundles/lang-overrides';


// These framework bundles have to be imported first
import 'oskari-bundle!oskari-frontend/bundles/framework/mapfull';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/oskariui/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/divmanazer/bundle.js';

// Then import mapmodule and rest of the application
import 'oskari-bundle!oskari-frontend/bundles/mapping/mapmodule/map2d_ol';



import 'oskari-bundle!oskari-frontend/bundles/mapping/mapmyplaces';
import 'oskari-bundle!oskari-frontend/bundles/framework/myplacesimport/mapuserlayers';
import 'oskari-bundle!oskari-frontend/bundles/mapping/maparcgis';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/ui-components/bundle.js';
import 'oskari-bundle!oskari-frontend/bundles/mapping/userstyle';
import 'oskari-bundle!oskari-frontend/bundles/framework/layeranalytics';

import 'oskari-bundle!oskari-frontend/bundles/mapping/drawtools';
import 'oskari-bundle!oskari-frontend/bundles/mapping/toolbar';
import 'oskari-bundle!oskari-frontend/bundles/framework/statehandler';
import 'oskari-bundle!oskari-frontend/bundles/mapping/infobox';

import 'oskari-bundle!oskari-frontend/bundles/framework/backendstatus';
import 'oskari-bundle!oskari-frontend/bundles/framework/search';
import 'oskari-bundle!oskari-frontend/bundles/framework/layerlist';
import 'oskari-bundle!oskari-frontend/bundles/framework/mydata';
import 'oskari-bundle!oskari-frontend/bundles/framework/publisher2';
import 'oskari-bundle!oskari-frontend/bundles/framework/maplegend';
import 'oskari-bundle!oskari-frontend/bundles/framework/userguide';

import 'oskari-bundle!oskari-frontend/bundles/catalogue/metadatasearch';
import 'oskari-bundle!oskari-frontend/bundles/catalogue/metadataflyout';
import 'oskari-bundle!oskari-frontend/bundles/framework/featuredata';

import 'oskari-bundle!oskari-frontend/bundles/framework/myplaces3';
import 'oskari-bundle!oskari-frontend/bundles/mapping/layerswipe';

//import 'oskari-bundle!oskari-frontend/bundles/framework/guidedtour';
import 'oskari-bundle!oskari-frontend/bundles/framework/printout';

//import 'oskari-bundle!oskari-frontend/bundles/framework/myplacesimport';
import 'oskari-bundle!oskari-frontend/bundles/framework/coordinatetool';
import 'oskari-bundle!oskari-frontend/bundles/mapping/maprotator';
import 'oskari-bundle!oskari-frontend/bundles/framework/timeseries';

// tampereeeeee
import 'oskari-bundle!oskari-frontend-contrib/bundles/tampere/search-from-channels';
import 'oskari-bundle!oskari-frontend-contrib/bundles/tampere/selected-featuredata';
import 'oskari-bundle!oskari-frontend/bundles/framework/announcements';
import 'oskari-lazy-bundle?content-editor!oskari-frontend-contrib/bundles/tampere/content-editor';

import 'oskari-bundle!../../bundles/file-upload';

import 'oskari-lazy-bundle?file-layerlist!../../bundles/file-layerlist';

import 'oskari-bundle!oskari-frontend-contrib/bundles/terrain-profile';

// /tampereeeeee

import 'oskari-lazy-bundle?admin-layereditor!oskari-frontend/bundles/admin/admin-layereditor';
import 'oskari-lazy-bundle?admin-announcements!oskari-frontend/bundles/admin/admin-announcements';
import 'oskari-lazy-bundle?admin-permissions!oskari-frontend/bundles/admin/admin-permissions';
import 'oskari-lazy-bundle?admin-users!oskari-frontend/bundles/admin/admin-users';
import 'oskari-lazy-bundle?admin!oskari-frontend/bundles/admin/admin';
import 'oskari-lazy-bundle?admin-wfs-search-channel!oskari-frontend-contrib/bundles/tampere/admin-wfs-search-channel';
import 'oskari-lazy-bundle?admin-layeranalytics!oskari-frontend/bundles/admin/admin-layeranalytics';

import '../../bundles/hacks.js';
import './css/overwritten.css';

import { addAccessLogNotice } from '../../bundles/accesslog/hook.js';

Oskari.on('app.start', () => {
    addAccessLogNotice();
});
