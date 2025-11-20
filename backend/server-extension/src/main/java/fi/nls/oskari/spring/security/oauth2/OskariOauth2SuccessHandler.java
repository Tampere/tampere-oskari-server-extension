package fi.nls.oskari.spring.security.oauth2;


import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.oskari.spring.security.OskariUserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Profile("oauth2")
@Component
public class OskariOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LogFactory.getLogger(OskariOauth2SuccessHandler.class);

    private final OskariUserHelper helper = new OskariUserHelper();

    @Autowired
    public OskariOauth2SuccessHandler() {
        super();
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof OidcUser oidcUser)) {
            throw new IllegalArgumentException("Expected DefaultOidcUser, got: " + principal.getClass().getName());
        }

        super.onAuthenticationSuccess(request, response, authentication);
        helper.onAuthenticationSuccess(request, response, oidcUser.getName());
    }


}