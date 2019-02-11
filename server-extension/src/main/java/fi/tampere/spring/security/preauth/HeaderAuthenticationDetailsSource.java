package fi.tampere.spring.security.preauth;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

public class HeaderAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

    /**
     * @param context the {@code HttpServletRequest} object.
     * @return the {@code WebAuthenticationDetails} containing information about the
     * current request
     */
    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new HeaderAuthenticationDetails(context);
    }
}