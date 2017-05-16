package eu.fraho.spring.securityJwt.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class JwtUser implements UserDetails, CredentialsContainer {
    private Long id = -1L;
    private String username = "anonymousUser";
    @JsonIgnore
    private String password = null;
    @JsonIgnore
    private Optional<String> totpSecret = Optional.empty();
    private boolean accountNonExpired = false;
    private boolean accountNonLocked = false;
    private boolean credentialsNonExpired = false;
    private boolean enabled = false;
    @JsonIgnore
    private List<GrantedAuthority> authorities = Collections.emptyList();
    private String authority = "NONE";
    private boolean apiAccessAllowed = false;

    public static JwtUser fromClaims(JWTClaimsSet claims) {
        JwtUser user = new JwtUser();
        user.setUsername(claims.getSubject());
        user.setAuthority(String.valueOf(claims.getClaim("authority")));
        user.setId(Long.valueOf(String.valueOf(claims.getClaim("uid"))));
        user.setAuthorities(Arrays.asList(new SimpleGrantedAuthority(user.getAuthority())));
        return user;
    }

    @Override
    public void eraseCredentials() {
        password = null;
        totpSecret = null;
    }

    public JWTClaimsSet.Builder toClaims() {
        return new JWTClaimsSet.Builder()
                .subject(getUsername())
                .claim("uid", getId())
                .claim("authority", getAuthority());
    }
}