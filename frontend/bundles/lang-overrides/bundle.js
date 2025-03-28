/**
 * Definition for bundle. See source for details.
 *
 * @class Oskari.language.Bundle
 */
Oskari.clazz.define(
    'Oskari.language.Bundle',
    function () {},
    {
        create: function () {
            return this;
        },
        start: function () {},
        stop: function () {}
    },
    {
        source: {
            scripts: [],
            locales: [
                {
                    lang: 'fi',
                    type: 'text/javascript',
                    src: './resources/locale/fi.js'
                },
                {
                    lang: 'sv',
                    type: 'text/javascript',
                    src: './resources/locale/sv.js'
                },
                {
                    lang: 'en',
                    type: 'text/javascript',
                    src: './resources/locale/en.js'
                }
            ]
        }
    }
);

Oskari.bundle_manager.installBundleClass(
    'lang-overrides',
    'Oskari.language.Bundle'
);
