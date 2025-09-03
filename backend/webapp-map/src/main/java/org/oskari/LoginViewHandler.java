package org.oskari;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.spring.SpringEnvHelper;
import fi.nls.oskari.spring.extension.OskariParam;
import fi.nls.oskari.spring.security.TreIntraDbloginFilterConfiguration;
import fi.nls.oskari.util.PropertyUtil;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginViewHandler {

    @Nullable
    private final TreIntraDbloginFilterConfiguration config;

    private final SpringEnvHelper env;

    @Autowired
    public LoginViewHandler(WebSecurityConfigurerAdapter genericAdapter, SpringEnvHelper env) {
        this.env = env;
        if (genericAdapter instanceof TreIntraDbloginFilterConfiguration) {
            this.config = (TreIntraDbloginFilterConfiguration) genericAdapter;
        } else {
            this.config = null;
        }
    }

    @RequestMapping("/tre-login")
    public String redirectToFileDL(Model model, @OskariParam ActionParameters params) throws Exception {
        model.addAttribute("_src_ip", params.getRequest().getRemoteAddr());

        if (config == null || !params.getUser().isGuest()) {
            // If filtering is not enabled, or user is logged in, redirect to frontpage
            params.getResponse().sendRedirect(params.getRequest().getContextPath());
            return "";
        }
        if(!config.isAllowedIp(params.getRequest())){
            params.getResponse().sendRedirect(params.getRequest().getContextPath() + "?loginState=failed");
            return "";
        }

        if (env.isDBLoginEnabled()) {
            model.addAttribute("_login_uri", env.getLoginUrl());
            model.addAttribute("_login_field_user", env.getParam_username());
            model.addAttribute("_login_field_pass", env.getParam_password());
        }
        return "tre-login";
    }
}