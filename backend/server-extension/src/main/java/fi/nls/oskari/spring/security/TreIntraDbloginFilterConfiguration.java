package fi.nls.oskari.spring.security;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.spring.SpringEnvHelper;
import fi.nls.oskari.spring.security.database.OskariAuthenticationProvider;
import fi.nls.oskari.spring.security.database.OskariAuthenticationSuccessHandler;
import fi.nls.oskari.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Profile("TreLoginWhitelistConfig")
//@Configuration
@Configuration
@EnableWebSecurity
@Order(1)
public class TreIntraDbloginFilterConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LogFactory.getLogger(TreIntraDbloginFilterConfiguration.class);
    private final SpringEnvHelper env;
    private final List<IpAddressMatcher> ipLoginWhitelist;

    @Autowired
    public TreIntraDbloginFilterConfiguration(SpringEnvHelper envHelper) {
        this.env = envHelper;
        ipLoginWhitelist = Arrays.stream(PropertyUtil.get("login.ip.whitelist", "").trim().split(",")).map(IpAddressMatcher::new).collect(Collectors.toList());

    }

    protected void configure(HttpSecurity http) throws Exception {
        logger.info("Configuring database login with Tampere intra filter");
        /*
         * We want to permitAll since guests can access everything. What makes the login work is that:
         * - loginProcessingUrl is the login form action url
         * - passwordParameter/usernameParameter matches the login form fields
         * - loginPage might not be needed since we permit all URLs
         */
        http.authenticationProvider(new OskariAuthenticationProvider());
        http.headers().frameOptions().disable();

        // 3rd party cookie blockers don't really work with cookie based CSRF protection on embedded maps.
        // Configure nginx to attach SameSite-flag to cookies instead.
        http.csrf().disable();

        // IMPORTANT! Only antMatch for processing url, otherwise SAML security filters are passed even if both are active
        http.formLogin()
                .loginProcessingUrl(env.getLoginUrl())
                .passwordParameter(env.getParam_password())
                .usernameParameter(env.getParam_username())
                .failureHandler(new OskariLoginFailureHandler("/?loginState=failed"))
                .successHandler(new OskariAuthenticationSuccessHandler())
                .loginPage("/");

        logger.warn("Adding Tampere internal network filter to loginurl: " + env.getLoginUrl());
        http.antMatcher(env.getLoginUrl()).addFilterBefore(treIpFilter, UsernamePasswordAuthenticationFilter.class);

    }

    private final Filter treIpFilter = new OncePerRequestFilter() {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            logger.info("Executing filter for login requests to URI " + request.getRequestURI() + " from IP: " + request.getRemoteAddr());
            if (isAllowedIp(request)) {
                filterChain.doFilter(request, response);
            }
            logger.warn("Logging not allowed for IP: " + request.getRemoteAddr());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Login forbidden from this network");
        }

    };


    public boolean isAllowedIp(HttpServletRequest request) {
        for (IpAddressMatcher ipAddressMatcher : ipLoginWhitelist) {
            if (ipAddressMatcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}
