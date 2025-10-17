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
import org.oskari.spring.security.database.OskariAuthenticationProvider;
import org.oskari.spring.security.database.OskariDatabaseSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
@Order(1)
public class TreIntraDbloginFilterConfiguration extends OskariDatabaseSecurityConfig {

    private static final Logger logger = LogFactory.getLogger(TreIntraDbloginFilterConfiguration.class);
    private final List<IpAddressMatcher> ipLoginWhitelist;
    private final SpringEnvHelper envHelper;


    @Autowired
    public TreIntraDbloginFilterConfiguration(SpringEnvHelper env,
                                              OskariAuthenticationProvider oskariAuthenticationProvider,
                                              OskariAuthenticationSuccessHandler oskariAuthenticationSuccessHandler) {
        super(env, oskariAuthenticationProvider, oskariAuthenticationSuccessHandler);
      this.envHelper = env;
        this.ipLoginWhitelist = Arrays.stream(PropertyUtil.get("login.ip.whitelist", "").trim().split(",")).map(IpAddressMatcher::new).collect(Collectors.toList());
    }

    @Bean
    @Order(1)
    @Override
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.warn("Adding Tampere internal network filter to loginurl: " + envHelper.getLoginUrl());
        http.addFilterBefore(treIpFilter, UsernamePasswordAuthenticationFilter.class);
        return super.securityFilterChain(http);

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
