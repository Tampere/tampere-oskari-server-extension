package fi.nls.oskari.spring;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.spring.extension.OskariParam;
import fi.nls.oskari.spring.security.preauth.UserDetailsHelper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Profile("preauth-debug")
@Controller
@RequestMapping("/auth")
public class AuthDebugHandler {

    private final static Logger LOG = LogFactory.getLogger(AuthDebugHandler.class);

    /*
        Headers:
        "X_EMAIL":"asdf@asdf.fi"
        "X_FIRSTNAME":"asdf",
        "X_LASTNAME":"0xNNNNNNNN",
        "X_MPO.ROLES":"24,124"
    */
    @RequestMapping
    public @ResponseBody
    Map<String,String> index(@OskariParam ActionParameters params) throws Exception {
        LOG.info("User logged in:", params.getRequest().getHeader("X_EMAIL"));
        Enumeration<String> names = params.getRequest().getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while(names.hasMoreElements()) {
            final String key = names.nextElement().toLowerCase();
            if(key.startsWith("x_") || "true".equalsIgnoreCase(params.getHttpParam("all"))) {
                headers.put(key, UserDetailsHelper.getHeader(params.getRequest(), key));
            }
        }
        return headers;
    }

}