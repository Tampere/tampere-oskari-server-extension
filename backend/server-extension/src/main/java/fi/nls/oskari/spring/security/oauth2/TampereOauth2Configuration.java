package fi.nls.oskari.spring.security.oauth2;


import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.oskari.spring.SpringEnvHelper;
import org.oskari.spring.security.OskariLoginFailureHandler;
import org.oskari.spring.security.OskariSpringSecurityDsl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity()
@Profile("oauth2")
@Order(100)

public class TampereOauth2Configuration {
    private static final Logger log = LogFactory.getLogger(TampereOauth2Configuration.class);

    private final SpringEnvHelper env;
    private final OskariTreOidcUserService oidcUserService;
    private final OskariOauth2SuccessHandler successHandler;

    @Autowired
    public TampereOauth2Configuration(
            SpringEnvHelper env,
            OskariOauth2SuccessHandler successHandler,
            OskariTreOidcUserService oidcUserService
    ) {
        this.env = env;
        this.successHandler = successHandler;
        this.oidcUserService = oidcUserService;
    }

    @Bean
    @Order(100)
    public SecurityFilterChain tampereOauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Oauth2 login");
        http.with(OskariSpringSecurityDsl.oskariCommonDsl(),
                (dsl) -> dsl
                        .setLogoutUrl(env.getLogoutUrl())
                        .setLogoutSuccessUrl(env.getLoggedOutPage())
        );
        OskariSpringSecurityDsl.oskariCommonDsl().init(http);

        // Add custom authentication provider
        http.authorizeHttpRequests(
                        // the user can access any url without logging in (guests can see geoportal)
                        // but we want to be explicit about it to have the user available on any request
                        authorize -> authorize
                                .requestMatchers("/oauth2/**", "/auth", "/login").authenticated()
                                .anyRequest().permitAll()
                )
                .oauth2Login(o -> o
                                // .loginProcessingUrl(env.getLoginUrl())
                                .failureHandler(new OskariLoginFailureHandler("/?loginState=failed"))
                                .userInfoEndpoint(u -> u.oidcUserService(oidcUserService))
                                .successHandler(successHandler)
                        //   .loginPage("/")
                );


        return http.build();
    }


}

