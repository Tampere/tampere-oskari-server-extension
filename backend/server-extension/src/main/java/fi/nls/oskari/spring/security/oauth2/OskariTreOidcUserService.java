package fi.nls.oskari.spring.security.oauth2;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.UserService;
import fi.nls.oskari.user.DatabaseUserService;
import fi.nls.oskari.util.PropertyUtil;
import org.oskari.spring.security.OskariUserHelper;
import org.oskari.user.Role;
import org.oskari.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@Profile("oauth2")
public class OskariTreOidcUserService extends OidcUserService {
    private static final Logger logger = LogFactory.getLogger(OskariTreOidcUserService.class);

    private final DatabaseUserService userService;
    private final boolean autoregisterOauthUsers;
    private final boolean copyRolesFromAD;
    private final TreEntraUtils treEntraUtils;

    @Autowired
    public OskariTreOidcUserService(TreEntraUtils treEntraUtils) {
        this.userService = getUserService();
        this.autoregisterOauthUsers = PropertyUtil.getOptional("oskari.oauth2.autoregister", false);
        this.copyRolesFromAD = PropertyUtil.getOptional("oskari.oauth2.copyAdRole", false);
        this.treEntraUtils = treEntraUtils;
        logger.info("Initialized TreOidcUserService with autoregister{}, copyRoles{]", autoregisterOauthUsers, copyRolesFromAD);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        try {
            User user = getUser(oidcUser);
            if (user == null) {
                throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
            }
            return new OskariTreOidcUser(oidcUser, user.getScreenname(), OskariUserHelper.getRoles(user.getRoles()));

        } catch (ServiceException e) {
            logger.warn("Error loading user by email {}", oidcUser, e);
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.SERVER_ERROR);

    }


    private User getUser(OidcUser oidcUser) throws ServiceException {
        User user = userService.getUserByEmail(oidcUser.getEmail());
        logger.warn("Loaded user {} from database with email {}", user, oidcUser.getEmail());
        if (autoregisterOauthUsers && user == null) {
            user = new User();
            user.setCreated(OffsetDateTime.now());
            user.addRole(Role.getDefaultUserRole());
            // TODO: Add roles from OidcUser?
        }
        Role defaultRole = Role.getDefaultUserRole();
        if (user == null || !user.hasRole(defaultRole.getName())) {
            if (user != null) {
                logger.debug("User {} does not have default role {}, Users roles {}", user, defaultRole.getName(), user.getRoles());
            }
            return null;
        }

        if (this.copyRolesFromAD) {
            treEntraUtils.fixRolesFromEntraid(user, oidcUser);
        }
        // Update user info
        if (copyInfoToUser(user, oidcUser)) {
            userService.saveUser(user);
        }

        return user;
    }

    protected DatabaseUserService getUserService() {
        try {
            // throws class cast exception if configured other than intended
            return (DatabaseUserService) UserService.getInstance();
        } catch (ServiceException e) {
            throw new RuntimeException("Error getting UserService. Is it configured?", e);
        }


    }

    private boolean copyInfoToUser(User user, OidcUser oidcUser) {
        boolean modified = false;
        final String email = Optional.ofNullable(oidcUser.getEmail()).orElse("").toLowerCase().trim();
        if (!email.isBlank() && !Objects.equals(user.getEmail(), email)) {
            user.setEmail(email);
            modified = true;
        }
        // If user has no given and family names set, it is still possible that they
        // have full name. Fallback to use it as firstname
        if ((oidcUser.getGivenName() == null || oidcUser.getGivenName().isBlank())
                && (oidcUser.getFamilyName() == null || oidcUser.getFamilyName().isBlank())
        ) {
            if (!Objects.equals(user.getFirstname(), oidcUser.getFullName())) {
                user.setFirstname(oidcUser.getFullName());
                modified = true;
            }
        } else {
            if (!Objects.equals(user.getFirstname(), oidcUser.getGivenName())) {
                user.setFirstname(oidcUser.getGivenName());
                modified = true;
            }
            if (!Objects.equals(user.getLastname(), oidcUser.getFamilyName())) {
                user.setLastname(oidcUser.getFamilyName());
                modified = true;
            }
        }

        String preferredUsername = oidcUser.getPreferredUsername();
        if (preferredUsername == null || preferredUsername.isBlank()) {
            preferredUsername = email;
        } else {
            preferredUsername = preferredUsername.toLowerCase();
        }
        if (preferredUsername.equals(user.getScreenname())) {
            user.setScreenname(preferredUsername);
            modified = true;
        }
        return modified;
    }
}
