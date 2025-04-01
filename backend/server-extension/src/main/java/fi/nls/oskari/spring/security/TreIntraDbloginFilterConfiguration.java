package fi.nls.oskari.spring.security;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.spring.SpringEnvHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Profile("TreUsernameFilter")
@Configuration
public class TreIntraDbloginFilterConfiguration {

    private static final Logger logger = LogFactory.getLogger(TreIntraDbloginFilterConfiguration.class);
    private final SpringEnvHelper envHelper;

    public TreIntraDbloginFilterConfiguration(SpringEnvHelper envHelper) {
        this.envHelper = envHelper;
    }

    private static final Filter treIpFilter = new OncePerRequestFilter() {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            logger.info("Executing filter for login requests to URI " + request.getRequestURI() + " from IP: " + request.getRemoteAddr());
            if (!request.getRemoteAddr().startsWith("10.")) {
                logger.warn("Logging not allowed for IP: " + request.getRemoteAddr());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Login forbidden from this network");
                return;
            }
            filterChain.doFilter(request, response);
        }

    };

    @Bean
    public SecurityFilterChain addTampereInternalNetworkFilter(HttpSecurity http) throws Exception {
        logger.warn("Adding Tampere internal network filter");
        http.antMatcher(envHelper.getLoginUrl()).addFilterBefore(treIpFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
