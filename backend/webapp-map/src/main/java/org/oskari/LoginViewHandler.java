package org.oskari;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.spring.SpringEnvHelper;
import fi.nls.oskari.spring.extension.OskariParam;
import fi.nls.oskari.spring.security.TreIntraDbloginFilterConfiguration;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
public class LoginViewHandler {

    @Nullable
    private final TreIntraDbloginFilterConfiguration config;

    private final SpringEnvHelper env;

    @Autowired
    public LoginViewHandler(Collection<WebSecurityConfigurerAdapter> genericAdapters, SpringEnvHelper env) {
        this.env = env;
        TreIntraDbloginFilterConfiguration filterCfg = null;
        for (WebSecurityConfigurerAdapter genericAdapter : genericAdapters) {
            if (genericAdapter instanceof TreIntraDbloginFilterConfiguration) {
                filterCfg = (TreIntraDbloginFilterConfiguration) genericAdapter;
                break;
            }
        }
        this.config = filterCfg;
    }

    @RequestMapping("/tre-login")
    public String redirectToFileDL(Model model, @OskariParam ActionParameters params) throws Exception {
        model.addAttribute("_src_ip", params.getRequest().getRemoteAddr());

        if (config == null || !params.getUser().isGuest()) {
            // If filtering is not enabled, or user is logged in, redirect to frontpage
            params.getResponse().sendRedirect(params.getRequest().getContextPath());
            return "";
        }
        if (!config.isAllowedIp(params.getRequest())) {
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