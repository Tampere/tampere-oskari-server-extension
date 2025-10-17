import 'oskari-loader!../../bundles/lang-overrides/bundle.js';

// These framework bundles have to be imported first
import 'oskari-bundle!oskari-frontend/bundles/framework/mapfull';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/oskariui/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/divmanazer/bundle.js';

// Then import mapmodule and rest of the application
import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapmodule/bundle.js';

import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapwmts/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/wfsvector/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapmyplaces/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapuserlayers/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/maparcgis/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/ui-components/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/userstyle/bundle.js';
import 'oskari-bundle!oskari-frontend/bundles/framework/layeranalytics';

import 'oskari-loader!oskari-frontend/packages/mapping/ol/drawtools/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/toolbar/bundle.js';
import 'oskari-bundle!oskari-frontend/bundles/framework/statehandler';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/infobox/bundle.js';

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
import 'oskari-loader!oskari-frontend/packages/mapping/ol/layerswipe/bundle.js';

//import 'oskari-bundle!oskari-frontend/bundles/framework/guidedtour';
import 'oskari-bundle!oskari-frontend/bundles/framework/printout';

//import 'oskari-bundle!oskari-frontend/bundles/framework/myplacesimport';
import 'oskari-bundle!oskari-frontend/bundles/framework/coordinatetool';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/maprotator/bundle.js';
import 'oskari-bundle!oskari-frontend/bundles/framework/timeseries';

// tampereeeeee
import 'oskari-loader!oskari-frontend-contrib/packages/tampere/bundle/search-from-channels/bundle.js';
import 'oskari-loader!oskari-frontend-contrib/packages/tampere/bundle/selected-featuredata/bundle.js';
import 'oskari-bundle!oskari-frontend/bundles/framework/announcements';
import 'oskari-lazy-loader?content-editor!oskari-frontend-contrib/packages/tampere/bundle/content-editor/bundle.js';

import 'oskari-loader!../../bundles/file-upload/bundle.js';

import 'oskari-lazy-loader?file-layerlist!../../bundles/file-layerlist/bundle.js';

import 'oskari-bundle!oskari-frontend-contrib/bundles/terrain-profile';

// /tampereeeeee

import 'oskari-lazy-bundle?admin-layereditor!oskari-frontend/bundles/admin/admin-layereditor';
import 'oskari-lazy-bundle?admin-announcements!oskari-frontend/bundles/admin/admin-announcements';
import 'oskari-lazy-bundle?admin-permissions!oskari-frontend/bundles/admin/admin-permissions';
import 'oskari-lazy-bundle?admin-users!oskari-frontend/bundles/admin/admin-users';
import 'oskari-lazy-bundle?admin!oskari-frontend/bundles/admin/admin';
import 'oskari-lazy-loader?admin-wfs-search-channel!oskari-frontend-contrib/packages/tampere/bundle/admin-wfs-search-channel/bundle.js';
import 'oskari-lazy-bundle?admin-layeranalytics!oskari-frontend/bundles/admin/admin-layeranalytics';

import '../../bundles/hacks.js';
import './css/overwritten.css';

import { addAccessLogNotice } from '../../bundles/accesslog/hook.js';

Oskari.on('app.start', () => {
    addAccessLogNotice();
});
