package fi.nls.oskari.spring.security.oauth2;


import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.UserService;
import fi.nls.oskari.user.DatabaseUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.oskari.log.AuditLog;
import org.oskari.spring.security.OskariUserHelper;
import org.oskari.user.Role;
import org.oskari.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TreEntraUtils extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LogFactory.getLogger(TreEntraUtils.class);

    private final OAuth2AuthorizedClientService clientService;
    private final ClientRegistration clientRegistration;

    private final OskariUserHelper helper = new OskariUserHelper();
    private final EntraIDGraphApiClient entraApiClient;

    public TreEntraUtils(
            OAuth2AuthorizedClientService clientService,
            EntraIDGraphApiClient entraApiClient,
            ClientRegistration clienRegistration
    ) {
        super();
        this.clientService = clientService;
        this.entraApiClient = entraApiClient;
        this.clientRegistration = clienRegistration;
    }

    private static final String ATTR7_ROLE_PREFIX = "ROLE_";



    /**
     * ATTR7 is the users organisation unit id in EntraID
     *
     * 1. Remove existing roles having ATTR7-prefix
     * 2. Fetch ATTR7 info from EntraID API.
     * 3. Add the roles from ATTR7 to the user.
     */
    private void fixRolesFromEntraid(User user, DefaultOidcUser oidcUser) {
        OAuth2AuthorizedClient authorizedClient = clientService.loadAuthorizedClient(clientRegistration.getRegistrationId(), oidcUser.getName());
        if (authorizedClient != null) {
            @NotNull String attr7 = entraApiClient.getExtensionAttribute7(authorizedClient.getAccessToken().getTokenValue());
            user.setRoles(Stream.concat(
                            // Remove roles with the dynamic prefix
                            user.getRoles().stream().filter(t -> !t.getName().startsWith(ATTR7_ROLE_PREFIX)),
                            // Re-add roles from the attr7 ( organisation id )
                            Arrays.stream(attr7.split(","))
                                    .map(String::trim)
                                    .filter(r -> !r.isEmpty())
                                    .map(r -> {
                                        Role role = new Role();
                                        role.setName(ATTR7_ROLE_PREFIX + r);
                                        return role;
                                    }))
                    .collect(Collectors.toSet()));
        } else {
            logger.warn("Authorized client is null when fetching organisation id for user ", user);
        }

        // Make sure user has the defaultUser role.
        final Role defaultUserRole = Role.getDefaultUserRole();
        if(user.getRoles().stream().noneMatch(t -> t.getName().equals(defaultUserRole.getName()))) {
            user.addRole(Role.getDefaultUserRole());
        }
    }


}