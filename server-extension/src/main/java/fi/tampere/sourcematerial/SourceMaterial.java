package fi.tampere.sourcematerial;

import java.util.HashSet;
import java.util.Set;

public class SourceMaterial {
    public static final String ROLE_PREFIX = "LAHTO_";

    /**
     * From current db. There's no good way of configuring these so hard-coding ids
     * that match the envs is ok'ish.
     * Later on we can return a list from PropertyUtil or do something with the db if this needs updating
     * @return
     */
    /*
    URL: https://georaster.tampere.fi/geoserver/gwc/service/wmts?
    Layers:
    - georaster:opaskartta_EPSG_3067
    - georaster:Ortokoonti_EPSG_3067
    - georaster:kantakartta_EPSG_3067
    - georaster:virastokartta_vari_EPSG_3067
     */
    public static Set<Long> getBaseLayerIds() {
        Set<Long> ids = new HashSet<>(4);
        ids.add(1918L);
        ids.add(2266L);
        ids.add(6L);
        ids.add(18L);
        return ids;
    }
}
