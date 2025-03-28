import 'oskari-loader!../../bundles/lang-overrides/bundle.js';

// These framework bundles have to be imported first
import 'oskari-loader!oskari-frontend/packages/framework/bundle/mapfull/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/oskariui/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/divmanazer/bundle.js';

// Then import mapmodule and rest of the application - 3D mapmodule
import 'oskari-loader!oskari-frontend/packages/mapping/olcs/mapmodule/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/olcs/map3dtiles/bundle.js';

import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapwmts/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/wfsvector/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapmyplaces/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/mapuserlayers/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/maparcgis/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/ui-components/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/userstyle/bundle.js';

import 'oskari-loader!oskari-frontend/packages/mapping/ol/drawtools/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/toolbar/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/statehandler/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/ol/infobox/bundle.js';

import 'oskari-loader!oskari-frontend/packages/framework/bundle/search/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/layerlist/bundle.js';
import 'oskari-loader!oskari-frontend/bundles/framework/mydata/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/maplegend/bundle.js';

import 'oskari-loader!oskari-frontend/packages/framework/bundle/coordinatetool/bundle.js';
import 'oskari-loader!oskari-frontend/packages/catalogue/metadatacatalogue/bundle.js';
import 'oskari-loader!oskari-frontend/packages/catalogue/bundle/metadataflyout/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/featuredata2/bundle.js';

import 'oskari-loader!oskari-frontend/packages/framework/bundle/myplaces3/bundle.js';

// additional 3d bundles
import 'oskari-loader!oskari-frontend/packages/mapping/time-control-3d/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/camera-controls-3d/bundle.js';
import 'oskari-loader!oskari-frontend/packages/mapping/dimension-change/bundle.js';

// tampereeeeee
import 'oskari-loader!oskari-frontend-contrib/packages/tampere/bundle/search-from-channels/bundle.js';
// /tampereeeeee

import 'oskari-lazy-loader?admin-layereditor!oskari-frontend/packages/admin/bundle/admin-layereditor/bundle.js';
import 'oskari-lazy-loader?admin-announcements!oskari-frontend/packages/admin/bundle/admin-announcements/bundle.js';
import 'oskari-lazy-loader?admin-layerrights!oskari-frontend/packages/framework/bundle/admin-layerrights/bundle.js';
import 'oskari-lazy-loader?admin-users!oskari-frontend/packages/framework/bundle/admin-users/bundle.js';
import 'oskari-lazy-loader?admin!oskari-frontend/packages/admin/bundle/admin/bundle.js';
import 'oskari-lazy-loader?admin-wfs-search-channel!oskari-frontend-contrib/packages/tampere/bundle/admin-wfs-search-channel/bundle.js';

import '../../bundles/hacks.js';
import './css/overwritten.css';

import { addAccessLogNotice } from '../../bundles/accesslog/hook.js';

Oskari.on('app.start', () => {
    addAccessLogNotice();
});
