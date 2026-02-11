package fi.nls.oskari.spring.security.oauth2;


import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.oskari.spring.security.OskariUserHelper;
import org.oskari.user.Role;
import org.oskari.user.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.util.Arrays;
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
    public void fixRolesFromEntraid(User user, OidcUser oidcUser) {
        OAuth2AuthorizedClient authorizedClient = clientService.loadAuthorizedClient(clientRegistration.getRegistrationId(), oidcUser.getName());
        if (authorizedClient != null) {
            @NotNull String attr7 = entraApiClient.getExtensionAttribute7(authorizedClient.getAccessToken().getTokenValue());
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