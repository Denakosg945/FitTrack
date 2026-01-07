package gr.hua.fitTrack.core.security;


import gr.hua.fitTrack.core.model.PersonType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public class ApplicationUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final PersonType type;

    public ApplicationUserDetails(final Long id, final String email, final String passwordHash, final PersonType type) {
        if (id <= 0) throw new IllegalArgumentException();
        if (email == null) throw new NullPointerException();
        if (email.isBlank()) throw new IllegalArgumentException();

        if (passwordHash == null) throw new NullPointerException();
        if (passwordHash.isBlank()) throw new IllegalArgumentException();

        if (type == null) throw new NullPointerException();

        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.type = type;
    }

    public long personId() {
        return this.id;
    }

    public String passwordHash() {
        return this.passwordHash;
    }
    public PersonType type() {
        return this.type;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final String role = (this.type == PersonType.TRAINER) ? "ROLE_TRAINER" : "ROLE_CLIENT";
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public  boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

}
