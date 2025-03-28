package fi.nls.oskari.spring.security.oauth2;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class OauthConfig {
    private static final Logger logger = LogFactory.getLogger(OauthConfig.class);
    private final AtomicReference<ClientRegistration> entraid = new AtomicReference<>();

    @Bean
    public ClientRegistration getClientRegistration() {
        return entraid.accumulateAndGet(null, (oldid, newId) ->
                Objects.requireNonNullElseGet(oldid, this::registerEntraId)
        );
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        return new InMemoryClientRegistrationRepository(getClientRegistration());
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    private ClientRegistration registerEntraId() {
        final String entraidTenant = PropertyUtil.get("oskari.entraid.tenantId");
        final String entraIdAppid = PropertyUtil.get("oskari.entraid.appId");
        final String entraIdClientSecret = PropertyUtil.get("oskari.entraid.clientSecret");

        logger.warn("Registering entraid for tenant: ", entraidTenant);

        return ClientRegistrations
                .fromOidcIssuerLocation("https://login.microsoftonline.com/" + entraidTenant + "/v2.0")
                .scope("openid", "profile", "email", "User.Read")
                .clientId(entraIdAppid)
                .clientSecret(entraIdClientSecret)
                .build();
    }

}
