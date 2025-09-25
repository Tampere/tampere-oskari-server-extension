package org.oskari;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import jakarta.annotation.Nullable;
import org.oskari.spring.SpringEnvHelper;
import org.oskari.spring.extension.OskariParam;
import fi.nls.oskari.spring.security.TreIntraDbloginFilterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
public class LoginViewHandler {

    private static final Logger log = LogFactory.getLogger(LoginViewHandler.class);

    @Nullable
    private final TreIntraDbloginFilterConfiguration config;

    private final SpringEnvHelper env;

    @Autowired
    public LoginViewHandler(@Nullable TreIntraDbloginFilterConfiguration treLoginConfig , SpringEnvHelper env) {
        this.env = env;
        this.config = treLoginConfig;
    }

    @RequestMapping("/tre-login")
    public String treLogin(Model model, @OskariParam ActionParameters params) throws Exception {
        model.addAttribute("_src_ip", params.getRequest().getRemoteAddr());

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