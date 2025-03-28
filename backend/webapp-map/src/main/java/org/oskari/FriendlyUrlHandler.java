package org.oskari;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.spring.extension.OskariParam;
import fi.nls.oskari.util.PropertyUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FriendlyUrlHandler {

    private String filedlUUID = null;
    private String threedUUID = null;
    private String sourcematerialUUID = null;

    @RequestMapping("/lataus")
    public String redirectlatausToFileDL(Model model,
                                   @OskariParam ActionParameters params) throws Exception {
        return redirectToFileDL(PropertyUtil.getDefaultLanguage(), model, params);
    }

    @RequestMapping("/filedl")
    public String redirectToFileDL(Model model,
                                    @OskariParam ActionParameters params) throws Exception {
        return redirectToFileDL(PropertyUtil.getDefaultLanguage(), model, params);
    }

    @RequestMapping("/filedl/{lang}")
    public String redirectToFileDL(@PathVariable("lang") String lang,
                                    Model model,
                                    @OskariParam ActionParameters params) throws Exception {
        if(!isSupported(lang)) {
            lang = PropertyUtil.getDefaultLanguage();
        }

        String url = "/?lang=" + lang + "&uuid=" + getFileDL();
        return "redirect:" + attachQuery(url, params.getRequest().getQueryString());
    }

    @RequestMapping("/3d")
    public String redirectTo3D(Model model,
                                    @OskariParam ActionParameters params) throws Exception {
        return redirectTo3D(PropertyUtil.getDefaultLanguage(), model, params);
    }

    @RequestMapping("/3d/{lang}")
    public String redirectTo3D(@PathVariable("lang") String lang,
                                    Model model,
                                    @OskariParam ActionParameters params) throws Exception {
        if(!isSupported(lang)) {
            lang = PropertyUtil.getDefaultLanguage();
        }

        String url = "/?lang=" + lang + "&uuid=" + get3DGeoportal();
        return "redirect:" + attachQuery(url, params.getRequest().getQueryString());
    }

    @RequestMapping("/lahto")
    public String redirectToSourceMaterial(Model model,
                               @OskariParam ActionParameters params) throws Exception {
        return redirectToSourceMaterial(PropertyUtil.getDefaultLanguage(), model, params);
    }

    @RequestMapping("/lahto/{lang}")
    public String redirectToSourceMaterial(@PathVariable("lang") String lang,
                               Model model,
                               @OskariParam ActionParameters params) throws Exception {
        if(!isSupported(lang)) {
            lang = PropertyUtil.getDefaultLanguage();
        }

        String url = "/?lang=" + lang + "&uuid=" + getSourceMaterial();
        return "redirect:" + attachQuery(url, params.getRequest().getQueryString());
    }

    private boolean isSupported(String lang) {
        if (lang == null || lang.isEmpty()) {
            return false;
        }
        for(String l: PropertyUtil.getSupportedLanguages()) {
            if(l.equalsIgnoreCase(lang)) {
                return true;
            }
        }
        return false;
    }

    private String attachQuery(String path, String query) {
        if(query == null) {
            return path;
        }
        if(path.indexOf('?') == -1) {
            return path + "?" + query;
        }
        if(path.endsWith("?")) {
            return path + query;
        }
        return path + "&" + query;

    }

    private String getSourceMaterial() {
        if (sourcematerialUUID != null) {
            return sourcematerialUUID;
        }
        sourcematerialUUID = OskariComponentManager
                .getComponentOfType(ViewService.class)
                .getViewWithConf("Lähtöaineisto")
                .getUuid();
        return sourcematerialUUID;
    }

    private String getFileDL() {
        if (filedlUUID != null) {
            return filedlUUID;
        }
        filedlUUID = OskariComponentManager
                .getComponentOfType(ViewService.class)
                .getViewWithConf("Latauspalvelu")
                .getUuid();
        return filedlUUID;
    }

    private String get3DGeoportal() {
        if (threedUUID != null) {
            return threedUUID;
        }
        threedUUID = OskariComponentManager
                .getComponentOfType(ViewService.class)
                .getViewWithConf("Tampere 3D")
                .getUuid();
        return threedUUID;
    }
}