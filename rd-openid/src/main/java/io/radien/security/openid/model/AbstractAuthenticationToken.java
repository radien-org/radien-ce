package io.radien.security.openid.model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public abstract class AbstractAuthenticationToken implements Authentication {

    private final Collection<GrantedAuthority> authorities;

    private Object details;

    private boolean authenticated = false;

    /**
     * Creates a token with the supplied array of authorities.
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     * represented by this authentication object.
     */
    public AbstractAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = Collections.unmodifiableList(new ArrayList<>(authorities));
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        if (this.getPrincipal() instanceof Principal) {
            return ((Principal) this.getPrincipal()).getName();
        }
        return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object obj) {
        AbstractAuthenticationToken test = (AbstractAuthenticationToken) obj;
        if (!this.authorities.equals(test.authorities)) {
            return false;
        }
        if ((this.details == null) && (test.getDetails() != null)) {
            return false;
        }
        if ((this.details != null) && (test.getDetails() == null)) {
            return false;
        }
        if ((this.details != null) && (!this.details.equals(test.getDetails()))) {
            return false;
        }
        if ((this.getCredentials() == null) && (test.getCredentials() != null)) {
            return false;
        }
        if ((this.getCredentials() != null) && !this.getCredentials().equals(test.getCredentials())) {
            return false;
        }
        if (this.getPrincipal() == null && test.getPrincipal() != null) {
            return false;
        }
        if (this.getPrincipal() != null && !this.getPrincipal().equals(test.getPrincipal())) {
            return false;
        }
        return this.isAuthenticated() == test.isAuthenticated();
    }

    @Override
    public int hashCode() {
        int code = 31;
        for (GrantedAuthority authority : this.authorities) {
            code ^= authority.hashCode();
        }
        if (this.getPrincipal() != null) {
            code ^= this.getPrincipal().hashCode();
        }
        if (this.getCredentials() != null) {
            code ^= this.getCredentials().hashCode();
        }
        if (this.getDetails() != null) {
            code ^= this.getDetails().hashCode();
        }
        if (this.isAuthenticated()) {
            code ^= -37;
        }
        return code;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("Principal=").append(getPrincipal()).append(", ");
        sb.append("Credentials=[PROTECTED], ");
        sb.append("Authenticated=").append(isAuthenticated()).append(", ");
        sb.append("Details=").append(getDetails()).append(", ");
        sb.append("Granted Authorities=").append(this.authorities);
        sb.append("]");
        return sb.toString();
    }

}
