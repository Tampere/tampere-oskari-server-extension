import 'oskari-bundle!../../bundles/lang-overrides';


// These framework bundles have to be imported first
import 'oskari-bundle!oskari-frontend/bundles/framework/mapfull';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/oskariui/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/ui-components/bundle.js';
import 'oskari-loader!oskari-frontend/packages/framework/bundle/divmanazer/bundle.js';

import 'oskari-bundle!oskari-frontend/bundles/mapping/mapmodule/map2d_ol';


import 'oskari-bundle!oskari-frontend/bundles/mapping/maparcgis';
//import 'oskari-bundle!oskari-frontend/bundles/framework/myplacesimport/mapuserlayers';

import 'oskari-bundle!oskari-frontend/bundles/mapping/infobox';
import 'oskari-bundle!oskari-frontend/bundles/mapping/drawtools';
import 'oskari-bundle!oskari-frontend/bundles/mapping/toolbar';
import 'oskari-bundle!oskari-frontend/bundles/framework/publishedstatehandler';

import 'oskari-bundle!oskari-frontend/bundles/framework/coordinatetool';
import 'oskari-bundle!oskari-frontend/bundles/mapping/maprotator';
import 'oskari-bundle!oskari-frontend/bundles/framework/rpc';

import 'oskari-lazy-bundle?maplegend!oskari-frontend/bundles/framework/maplegend';
import 'oskari-lazy-bundle?featuredata!oskari-frontend/bundles/framework/featuredata';

import '../../bundles/hacks.js';
import './css/overwritten.css';
