package fi.nls.oskari.spring.security.oauth2;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.spring.extension.OskariParam;
import fi.nls.oskari.util.PropertyUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Profile("oauth2")
@Controller
@RequestMapping
public class Oauth2ApiController {


    private final static Logger LOG = LogFactory.getLogger(Oauth2ApiController.class);

    @RequestMapping("/auth")
    public RedirectView index(@OskariParam ActionParameters params, RedirectAttributes attributes) {
        if (params.getResponse().isCommitted()) {
            // to prevent errors in log -> request has already been handled
            return null;
        }
        String url = PropertyUtil.get("oskari.domain") + PropertyUtil.get("oskari.map.url");
        return new RedirectView(url);
    }

    @RequestMapping("/oauth2")
    public RedirectView oauth2(Model model, @OskariParam ActionParameters params) throws Exception {
        if (params.getResponse().isCommitted()) {
            // to prevent errors in log -> request has already been handled
            return null;
        }
        String url = PropertyUtil.get("oskari.domain") + PropertyUtil.get("oskari.map.url");
        return new RedirectView(url);
    }
    @RequestMapping("/login")
    public RedirectView login(Model model, @OskariParam ActionParameters params) throws Exception {
        if (params.getResponse().isCommitted()) {
            // to prevent errors in log -> request has already been handled
            return null;
        }
        String url = PropertyUtil.get("oskari.domain") + PropertyUtil.get("oskari.map.url");
        return new RedirectView(url);
    }


}
