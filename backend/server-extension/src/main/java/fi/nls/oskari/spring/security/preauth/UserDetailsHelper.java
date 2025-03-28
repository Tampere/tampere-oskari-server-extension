package fi.nls.oskari.spring.security.preauth;

import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.stream.Collectors;

/**
 * Created by SMAKINEN on 19.6.2017.
 */
public class UserDetailsHelper {

    private final static Logger LOG = LogFactory.getLogger(UserDetailsHelper.class);
    /**
     * Parses request headers for user info like:
     * "auth-email":"asdf@asdf.fi"
     * "auth-firstname":"asdf"
     * "auth-lastname":"0xNNNNNNNN"
     * "auth-screenname":"asdf"
     * "auth-nlsadvertisement":""
     */
    public static User parseUserFromHeaders(HttpServletRequest request) {

        User user = new User();
        user.setEmail(getHeader(request, "X_EMAIL"));
        user.setFirstname(getHeader(request, "X_FIRSTNAME"));
        user.setLastname(getHeader(request, "X_LASTNAME"));
        user.setScreenname(user.getEmail());

        String rolesHeader = getHeader(request, "X_MPO.ROLES");
        // set roles based on header
        if(rolesHeader != null) {
            user.setRoles(Arrays.asList(rolesHeader.split(","))
                    .stream()
                    .filter(r -> r != null)
                    .map(r -> r.trim())
                    .filter(r -> !r.isEmpty())
                    .map(r -> {
                        Role role = new Role();
                        role.setName("ROLE_" + r);
                        return role;
                    })
                    .collect(Collectors.toSet()));
        }

        // add default role for logged in user
        user.addRole(Role.getDefaultUserRole());
        LOG.info("Parsed roles for user (", user.getEmail(),"):", user.getRoles());

        return user;
    }

    public static String getHeader(HttpServletRequest request, String key) {
        String input = request.getHeader(key);
        if (input == null) {
            return null;
        }
        // Some values can be hex-encoded -> translate to "human readable"
        if (!input.startsWith("0x")) {
            return input;
        }
        input = input.substring(2);
        byte[] bytes = DatatypeConverter.parseHexBinary(input);
        return new String(bytes, Charset.forName("UTF-8"));
    }
}
