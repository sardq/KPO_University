package demo.Core.Models;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.util.Collections;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {

    private final String token;

    public ApiKeyAuthentication(String token) {
        super(Collections.emptyList());
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return "ApiKeyUser";
    }
}
