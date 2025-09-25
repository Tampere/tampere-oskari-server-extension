package fi.nls.oskari.spring.security.oauth2;


import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.oskari.spring.SpringEnvHelper;
import org.oskari.spring.security.OskariAuthenticationSuccessHandler;
import org.oskari.spring.security.OskariLoginFailureHandler;
import org.oskari.spring.security.OskariSpringSecurityDsl;
import org.oskari.spring.security.database.OskariAuthenticationProvider;
import org.oskari.spring.security.database.OskariDatabaseSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = false)
@Order(0)
@Profile("oauth2")
public class TampereOauth2Configuration {
    private static final Logger log = LogFactory.getLogger(TampereOauth2Configuration.class);

    private final OskariOauth2SuccessHandler successHandler;
    private final SpringEnvHelper env;

    @Autowired
    public TampereOauth2Configuration(SpringEnvHelper env,
                                      OskariOauth2SuccessHandler successHandler) {

        this.env = env;
        this.successHandler = successHandler;

    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Oauth2 login");
        http.with(OskariSpringSecurityDsl.oskariCommonDsl(),
                (dsl) -> dsl
                        .setLogoutUrl(env.getLogoutUrl())
                        .setLogoutSuccessUrl(env.getLoggedOutPage())
        );


        // Add custom authentication provider
        http.authorizeHttpRequests(
                        // the user can access any url without logging in (guests can see geoportal)
                        // but we want to be explicit about it to have the user available on any request
                        authorize -> authorize
                                .requestMatchers("/oauth2", "/auth", "/login").authenticated()
                                .anyRequest().permitAll()
                )
                .oauth2Login(o -> o
                        .loginProcessingUrl(env.getLoginUrl())
                        .failureHandler(new OskariLoginFailureHandler("/?loginState=failed"))
                        .successHandler(successHandler)
                        .loginPage("/")
                );

        return http.build();
    }


}

