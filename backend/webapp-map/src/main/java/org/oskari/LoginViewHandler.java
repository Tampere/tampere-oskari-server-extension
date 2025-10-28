package org.oskari;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.spring.security.TreIntraDbloginFilterConfiguration;
import fi.nls.oskari.util.PropertyUtil;
import jakarta.annotation.Nullable;
import org.oskari.spring.SpringEnvHelper;
import org.oskari.spring.extension.OskariParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginViewHandler {

    private static final Logger log = LogFactory.getLogger(LoginViewHandler.class);

    @Nullable
    private final TreIntraDbloginFilterConfiguration config;
    private final static String PROPERTY_CLIENT_DOMAIN = "oskari.client.domain";

    private final SpringEnvHelper env;
    private final String clientDomain;

    @Autowired
    public LoginViewHandler(@Nullable TreIntraDbloginFilterConfiguration treLoginConfig , SpringEnvHelper env) {
        this.env = env;
        this.config = treLoginConfig;
        clientDomain = PropertyUtil.get(PROPERTY_CLIENT_DOMAIN, "");

    }

    @RequestMapping("/tre-login")
    public String treLogin(Model model, @OskariParam ActionParameters params) throws Exception {
        model.addAttribute("_src_ip", params.getRequest().getRemoteAddr());
        model.addAttribute("clientDomain", clientDomain);

        if (config == null || !params.getUser().isGuest()) {
            log.info("User already logged in or login is not enabled. ");
            // If filtering is not enabled, or user is logged in, redirect to frontpage
            params.getResponse().sendRedirect(params.getRequest().getContextPath());
            return "tre-login";
        }
        if (!config.isAllowedIp(params.getRequest())) {
            log.info("IP not allowed to login: " + params.getRequest().getRemoteAddr() );
            params.getResponse().sendRedirect(params.getRequest().getContextPath() + "?loginState=failed");
            return "tre-login";
        }

        if (env.isDBLoginEnabled()) {
            model.addAttribute("_login_uri", env.getLoginUrl());
            model.addAttribute("_login_field_user", env.getParam_username());
            model.addAttribute("_login_field_pass", env.getParam_password());
        }
        return "tre-login";
    }
}