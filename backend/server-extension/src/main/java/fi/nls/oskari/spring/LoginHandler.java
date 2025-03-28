package fi.nls.oskari.spring;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.spring.extension.OskariParam;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by SMAKINEN on 2.6.2017.
 */
@Profile("preauth")
@Controller
public class LoginHandler {

    private final static Logger LOG = LogFactory.getLogger(LoginHandler.class);

    @RequestMapping("/login")
    public String index(Model model, @OskariParam ActionParameters params) throws Exception {
        if ("true".equalsIgnoreCase(params.getHttpParam("error"))) {
            model.addAttribute("error", true);
        }
        model.addAttribute("language", params.getLocale().getLanguage());
        return "login";
    }


}