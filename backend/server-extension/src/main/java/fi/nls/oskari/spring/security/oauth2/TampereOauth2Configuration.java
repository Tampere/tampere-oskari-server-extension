package fi.nls.oskari.spring.security.oauth2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity(debug = false)
@Order(0)
@Profile("oauth2")
public class TampereOauth2Configuration extends WebSecurityConfigurerAdapter {

    private final OskariOauth2SuccessHandler successHandler;

    @Autowired
    public TampereOauth2Configuration(OskariOauth2SuccessHandler successHandler) {
        this.successHandler = successHandler;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Don't set "X-Frame-Options: deny" header, that would prevent
        // embedded maps from working
        http.headers().frameOptions().disable();

        // Don't create unnecessary sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        // Disable HSTS header, we don't want to force HTTPS for ALL requests
        http.headers().httpStrictTransportSecurity().disable();

      //  String authorizeUrl = PropertyUtil.get("oskari.authorize.url", "/oauth2");

        // We want to force authorization for /oauth2 prefix, which will force login flow for user
        http.authorizeRequests().antMatchers("/oauth2", "/auth", "/login").authenticated();

        // Oauth2 requires return urls that include /oauth2 in their name, We want to pass those
        // urls to the oauth2Login customizer.
        http
                .antMatcher("/**/oauth2/**")
                .oauth2Login(Customizer.withDefaults())
                .oauth2Login().successHandler(successHandler);
    }


}

