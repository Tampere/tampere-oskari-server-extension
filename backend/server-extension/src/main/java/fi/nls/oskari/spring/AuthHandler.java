package fi.nls.oskari.spring;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.spring.extension.OskariParam;
import fi.nls.oskari.util.PropertyUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Profile("preauth")
@Controller
@RequestMapping("/auth")
public class AuthHandler {

    private final static Logger LOG = LogFactory.getLogger(AuthHandler.class);

    @RequestMapping
    public RedirectView index(@OskariParam ActionParameters params,
                              RedirectAttributes attributes) {
        LOG.info("User logged in:", params.getRequest().getHeader("X_EMAIL"));
        if(params.getResponse().isCommitted()) {
            // to prevent errors in log -> request has already been handled
            return null;
        }
        String url = PropertyUtil.get("oskari.domain") + PropertyUtil.get("oskari.map.url");
        return new RedirectView (url);
    }

}