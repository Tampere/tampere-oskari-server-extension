/**
 * Definition for bundle. See source for details.
 *
 * @class Oskari.file-layerlist.Bundle
 */
Oskari.clazz.define('Oskari.file-layerlist.Bundle',
    function () {},
    {
        create: function () {
            return Oskari.clazz.create('Oskari.file-layerlist.BundleInstance');
        },
        start: function () {},
        stop: function () {}
    },
    {
        source: {
            scripts: [
                {
                    type: 'text/javascript',
                    src: './instance.js'
                }
            ],
            locales: [
                {
                    lang: 'fi',
                    type: 'text/javascript',
                    src: './resources/locale/fi.js'
                }
            ]
        }
    }
);

Oskari.bundle_manager.installBundleClass(
    'file-layerlist',
    'Oskari.file-layerlist.Bundle'
);
