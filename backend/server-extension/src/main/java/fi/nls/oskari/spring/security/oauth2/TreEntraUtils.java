package fi.nls.oskari.spring.security.oauth2;


import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.oskari.user.Role;
import org.oskari.user.User;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Profile("oauth2")
public class TreEntraUtils extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LogFactory.getLogger(TreEntraUtils.class);

    private final OAuth2AuthorizedClientService clientService;
    private final ClientRegistration clientRegistration;

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
     * <p>
     * 1. Remove existing roles having ATTR7-prefix
     * 2. Fetch ATTR7 info from EntraID API.
     * 3. Add the roles from ATTR7 to the user.
     */
    public void fixRolesFromEntraid(User user, OAuth2AccessToken accessToken) {
        if (clientRegistration == null || clientRegistration.getRegistrationId() == null || clientRegistration.getRegistrationId().isBlank()) {
            logger.warn("Client registration is null or empty. Can not get roles from EntraID. {}", clientRegistration);
            return;
        }
        if (accessToken == null || accessToken.getTokenValue() == null) {
            logger.warn("accessToken null. Can not get roles from EntraID. '{}'", accessToken);
            return;
        }
        // OAuth2AuthorizedClient authorizedClient = clientService.loadAuthorizedClient(clientRegistration.getRegistrationId(), oidcUser.getName());

        @NotNull String attr7 = entraApiClient.getExtensionAttribute7(accessToken.getTokenValue());
        logger.info("Got attr7 for user {}: {}", user.getScreenname(), attr7);
        user.setRoles(Stream.concat(
                        // Add all non-dynamic roles
                        user.getRoles().stream().filter(t -> !t.getName().startsWith(ATTR7_ROLE_PREFIX)),
                        // add dynamic roles from the attr7 ( organisation id )
                        Arrays.stream(attr7.split(","))
                                .map(String::trim)
                                .filter(r -> !r.isEmpty())
                                .map(r -> {
                                    Role role = new Role();
                                    role.setName(ATTR7_ROLE_PREFIX + r);
                                    return role;
                                }))
                .collect(Collectors.toSet()));


        // Make sure user has the defaultUser role.
        final Role defaultUserRole = Role.getDefaultUserRole();
        if (user.getRoles().stream().noneMatch(t -> t.getName().equals(defaultUserRole.getName()))) {
            user.addRole(Role.getDefaultUserRole());
        }
    }


}