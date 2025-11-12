package fi.nls.oskari.spring.security.oauth2;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public record OskariTreOidcUser(OidcUser oidcUser, String username,
                                List<GrantedAuthority> authorities) implements OidcUser, Serializable {

    @Serial
    private static final long serialVersionUID = 2025111201L;
    private static final Logger logger = LogFactory.getLogger(OskariTreOidcUser.class);

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return username;
    }

}
