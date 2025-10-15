import 'oskari-loader!../../bundles/lang-overrides/bundle.js';

// main starting point
import 'oskari-bundle!oskari-frontend/bundles/framework/mapfull';

// jQuery-based UI components
import 'oskari-loader!oskari-frontend/packages/framework/bundle/oskariui/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/ui-components/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/divmanazer/bundle.js';

// jQuery-based UI components
import 'oskari-loader!oskari-frontend/packages/framework/bundle/oskariui/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/ui-components/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/divmanazer/bundle.js';

// 2D mapmodule and support for additional map layer types
import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapmodule/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapwmts/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/maparcgis/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/wfsvector/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapmyplaces/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapuserlayers/bundle.js';

// additional map related bundles
import 'oskari-loader!oskari-frontend/packages/mapping/ol/drawtools/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/toolbar/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/infobox/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/heatmap/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/maprotator/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/userstyle/bundle.js';

// other bundles
import 'oskari-bundle!oskari-frontend/bundles/framework/coordinatetool';
import 'oskari-bundle!oskari-frontend/bundles/framework/guidedtour';
import 'oskari-bundle!oskari-frontend/bundles/framework/layerlist';
import 'oskari-bundle!oskari-frontend/bundles/framework/maplegend';
import 'oskari-bundle!oskari-frontend/bundles/framework/statehandler';
import 'oskari-bundle!oskari-frontend/bundles/framework/timeseries';
import 'oskari-bundle!oskari-frontend/bundles/framework/userguide';
import 'oskari-bundle!oskari-frontend/bundles/framework/findbycoordinates';

// support for 3D-layer type and 2d/3d switcher
//import 'oskari-loader!oskari-frontend/packages/mapping/olcs/map3dtiles/bundle.js';
//import 'oskari-bundle!oskari-frontend/bundles/mapping/dimension-change';

// lazy-loaded as these are not used for every user (allows mobile version etc)
import 'oskari-lazy-bundle?metadatasearch!oskari-frontend/bundles/catalogue/metadatasearch';
import 'oskari-lazy-bundle?metadataflyout!oskari-frontend/bundles/catalogue/metadataflyout'
import 'oskari-lazy-bundle?search!oskari-frontend/bundles/framework/search';
import 'oskari-lazy-bundle?featuredata!oskari-frontend/bundles/framework/featuredata';
import 'oskari-lazy-bundle?language-selector!oskari-frontend/bundles/framework/language-selector';
import 'oskari-lazy-bundle?myplaces3!oskari-frontend/bundles/framework/myplaces3';
import 'oskari-lazy-bundle?myplacesimport!oskari-frontend/bundles/framework/myplacesimport';
import 'oskari-lazy-bundle?mydata!oskari-frontend/bundles/framework/mydata';
import 'oskari-lazy-bundle?publisher2!oskari-frontend/bundles/framework/publisher2';
import 'oskari-lazy-bundle?statsgrid!oskari-frontend/bundles/statistics/statsgrid';
import 'oskari-lazy-bundle?printout!oskari-frontend/bundles/framework/printout';

// admin functionalities (lazy-loaded)
import 'oskari-lazy-bundle?admin-layereditor!oskari-frontend/bundles/admin/admin-layereditor';
import 'oskari-lazy-bundle?admin-permissions!oskari-frontend/bundles/admin/admin-permissions';
import 'oskari-lazy-bundle?admin!oskari-frontend/bundles/admin/admin';
import 'oskari-lazy-bundle?metrics!oskari-frontend/bundles/admin/metrics';
import 'oskari-lazy-bundle?appsetup!oskari-frontend/bundles/admin/appsetup';
import 'oskari-lazy-bundle?admin-users!oskari-frontend/bundles/admin/admin-users';

// tampereeeeee
//import 'oskari-bundle!oskari-frontend-contrib/bundles/tampere/search-from-channels';
//import 'oskari-bundle!oskari-frontend-contrib/bundles/tampere/selected-featuredata';
import 'oskari-bundle!oskari-frontend/bundles/framework/announcements';
//import 'oskari-lazy-bundle?content-editor!oskari-frontend-contrib/bundles/tampere/content-editor';

import 'oskari-loader!../../bundles/file-upload/bundle.js';

import 'oskari-lazy-loader?file-layerlist!../../bundles/file-layerlist/bundle.js';

//import 'oskari-loader!oskari-frontend-contrib/packages/terrain-profile/bundle.js';

// /tampereeeeee

import 'oskari-lazy-bundle?admin-announcements!oskari-frontend/bundles/admin/admin-announcements';
//import 'oskari-lazy-bundle?admin-layerrights!oskari-frontend/bundles/framework/admin-layerrights';
//import 'oskari-lazy-bundle?admin-wfs-search-channel!oskari-frontend-contrib/bundles/tampere/admin-wfs-search-channel';
import 'oskari-lazy-bundle?admin-layeranalytics!oskari-frontend/bundles/admin/admin-layeranalytics';

//import '../../bundles/hacks.js';
import './css/overwritten.css';

import { addAccessLogNotice } from '../../bundles/accesslog/hook.js';

Oskari.on('app.start', () => {
    addAccessLogNotice();
});
