package org.oskari;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import org.apache.commons.collections4.map.LinkedMap;
import org.oskari.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
public class VersionHandler {

    private static final Logger logger = LogFactory.getLogger(VersionHandler.class);
    private final Properties gitProperties;

    public VersionHandler() {
        logger.debug("Initializing git status");
        this.gitProperties = new Properties();
        try (InputStream resource = getClass().getResourceAsStream("/git.properties")) {

            if (resource == null) {
                logger.warn("GIT Resource not found!");
                return;
            }

            this.gitProperties.load(resource);
            logger.debug("Git keys:", this.gitProperties.keys());
        } catch (IOException e) {
            logger.warn("Error initializing git proerties", e);
        }
    }


    @GetMapping("/versioninfo")
    public Map<String, String> getVersion() {
        Map<String, String> result = new LinkedMap<>();
        result.put("backendVersion", gitProperties.getProperty("git.build.version", "unknown"));
        result.put("frontendVersion", PropertyUtil.get("oskari.client.version"));

        Authentication user = SecurityContextHolder.getContext().getAuthentication();

        if (user == null || !user.isAuthenticated() || user.getName().equalsIgnoreCase("anonymousUser")) {
            result.put("loggedIn", "false");
            return result;
        }
        if (user.getPrincipal() instanceof OidcUser oidcUser) {
            result.put("usertype", "oidcUser");
            result.put("username", oidcUser.getPreferredUsername());
        } else if (user.getPrincipal() instanceof User oskariUser) {
            result.put("usertype", "oskariUser");
            result.put("username", oskariUser.getScreenname());
        } else if (user.getPrincipal() instanceof String) {
            result.put("usertype", "string");
            result.put("username", user.getPrincipal().toString());
        } else {
            result.put("usertype", user.getPrincipal().getClass().getName());
            result.put("username", user.getName());
            result.put("principal", user.getPrincipal().toString());
        }


        result.put("loggedIn", "true");
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase("user"))) {
            result.put("buildtime", gitProperties.getProperty("git.build.time", "unknown"));
            result.put("commitId", gitProperties.getProperty("git.commit.id.abbrev", "unknown"));
            result.put("dirty", gitProperties.getProperty("git.dirty", "unknown"));
            result.put("tag", gitProperties.getProperty("git.tag", "unknown"));
        }

        return result;
    }

}
