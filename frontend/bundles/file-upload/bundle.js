/**
 * Definition for bundle. See source for details.
 *
 * @class Oskari.file-upload.Bundle
 */
Oskari.clazz.define(
    'Oskari.file-upload.Bundle',
    function () {},
    {
        create: function () {
            return Oskari.clazz.create('Oskari.file-upload.BundleInstance');
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
    'file-upload',
    'Oskari.file-upload.Bundle'
);
