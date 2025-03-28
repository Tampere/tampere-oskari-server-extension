/**
 * Definition for bundle. See source for details.
 *
 * @class Oskari.sourcematerial.Bundle
 */
Oskari.clazz.define('Oskari.sourcematerial.Bundle',
    function () {},
    {
        create: function () {
            return Oskari.clazz.create('Oskari.sourcematerial.BundleInstance');
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
    'sourcematerial',
    'Oskari.sourcematerial.Bundle'
);
