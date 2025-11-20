package fi.nls.oskari.spring.security;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

import fi.nls.oskari.util.PropertyUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Profile("TreLoginWhitelistConfig")
//@Configuration
@Configuration
@EnableWebSecurity
@Order(10)
public class TreIntraDbloginFilterConfiguration {

    private static final Logger log = LogFactory.getLogger(TreIntraDbloginFilterConfiguration.class);
    private final SpringEnvHelper envHelper;
    private final OskariAuthenticationProvider oskariAuthenticationProvider;
    private final OskariAuthenticationSuccessHandler oskariAuthenticationSuccessHandler;
    private final @NotNull List<IpAddressMatcher> ipLoginWhitelist;


    @Autowired
    public TreIntraDbloginFilterConfiguration(SpringEnvHelper env,
                                              OskariAuthenticationProvider oskariAuthenticationProvider,

                                              OskariAuthenticationSuccessHandler oskariAuthenticationSuccessHandler) {
        this.envHelper = env;
        this.oskariAuthenticationProvider = oskariAuthenticationProvider;
        this.oskariAuthenticationSuccessHandler = oskariAuthenticationSuccessHandler;
        this.ipLoginWhitelist = Arrays.stream(PropertyUtil.get("login.ip.whitelist", "").split(","))
                .map(String::trim)
                .filter(t -> !t.isBlank())
                .map(IpAddressMatcher::new)
                .collect(Collectors.toList());
    }

    @Bean
    @Order(10)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.warn("Adding Tampere internal network filter to loginurl: " + envHelper.getLoginUrl());
        http.with(OskariSpringSecurityDsl.oskariCommonDsl(),  dsl -> dsl.setLoginFilter(treIpFilter));

        // Add custom authentication provider
        http.authenticationProvider(oskariAuthenticationProvider);
        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        );
        http
                .securityMatcher(envHelper.getLoginUrl(), "/basicauth")
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/basicauth").authenticated()
                                .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginProcessingUrl(envHelper.getLoginUrl())
                        .passwordParameter(envHelper.getParam_password())
                        .usernameParameter(envHelper.getParam_username())
                        .failureHandler(new OskariLoginFailureHandler("/?loginState=failed"))
                        .successHandler(oskariAuthenticationSuccessHandler)
                        .loginPage("/tre-login")
                );

        return http.build();

    }

    private final Filter treIpFilter = new OncePerRequestFilter() {

        @Override
        protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
            logger.info("Executing filter for login requests to URI " + request.getRequestURI() + " from IP: " + request.getRemoteAddr());
            if (!request.getRequestURI().equals(envHelper.getLoginUrl()) || isAllowedIp(request)) {
                filterChain.doFilter(request, response);
                return;
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
