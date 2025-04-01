package fi.nls.oskari.spring.security.preauth;

import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.UserService;
import fi.nls.oskari.user.DatabaseUserService;
import fi.nls.oskari.util.JSONHelper;
import fi.tampere.sourcematerial.SourceMaterial;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


public class OskariPreAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final DatabaseUserService userService;

    public OskariPreAuthenticationSuccessHandler() {
        this.userService = getUserService();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final Object principal = authentication.getPrincipal();

        if (!(principal instanceof OskariUserDetails)) {
            throw new IllegalArgumentException(
                    "Expected OskariUserDetails, "
                            + "got: " + principal.getClass().getName());
        }
        OskariUserDetails oud = (OskariUserDetails) principal;

        User user = null;
        try {
            user = getUser(oud);
            if (user.getId() == -1) {
                userService.saveUser(user);
                // the user returned by createUser() doesn't have roles so find the user with email
                user = userService.getUserByEmail(user.getEmail());
            } else {
                userService.saveUser(user);
            }
        } catch (ServiceException | RuntimeException e) {
            throw new AuthenticationServiceException(
                    "Unable to load/store Oskari user data from/to PostgreSQL",
                    e);
        }

        setupSession(user, request);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private User getUser(OskariUserDetails oud) throws ServiceException {
        User user = userService.getUserByEmail(oud.getUser().getEmail());
        // sdf is not threadsafe so create new for each login
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        if (user == null) {
            user = oud.getUser();
            user.setAttribute("created", format.format(new Date()));
        } else {
            // copy data that we got from headers
            user.setFirstname(oud.getUser().getFirstname());
            user.setLastname(oud.getUser().getLastname());
            user.setScreenname(oud.getUser().getScreenname());
            // Keep previous roles that start with the source material roles prefix.
            Set<Role> roles = user.getRoles().stream()
                    .filter(r -> r.getName().startsWith(SourceMaterial.ROLE_PREFIX))
                    .collect(Collectors.toSet());
            // add the roles we get from auth
            roles.addAll(oud.getUser().getRoles());
            user.setRoles(roles);
            // merge attributes
            JSONHelper.merge(user.getAttributesJSON(), oud.getUser().getAttributesJSON());
        }
        user.setAttribute("lastLogin", format.format(new Date()));
        return user;

    }

    protected void setupSession(User authenticatedUser, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        synchronized (session) {
            session.setAttribute(User.class.getName(), authenticatedUser);
        }
    }

    protected DatabaseUserService getUserService() {
        try {
            // throws class cast exception if configured other than intended
            return (DatabaseUserService) UserService.getInstance();
        } catch (ServiceException e) {
            throw new RuntimeException("Error getting UserService. Is it configured?", e);
        }
    }
}