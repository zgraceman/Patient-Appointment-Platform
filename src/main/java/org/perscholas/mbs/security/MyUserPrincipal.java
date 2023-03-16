package org.perscholas.mbs.security;

import org.perscholas.mbs.models.AuthGroup;
import org.perscholas.mbs.models.Patient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MyUserPrincipal implements UserDetails {

    Patient patient;
    List<AuthGroup> authGroup;

    public MyUserPrincipal(Patient patient, List<AuthGroup> authGroup) {
        this.patient = patient;
        this.authGroup = authGroup;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authGroup.stream().map( auth -> new SimpleGrantedAuthority(auth.getRole())).collect(Collectors.toList());

    }

    @Override
    public String getPassword() {
        return patient.getPassword();
    }

    @Override
    public String getUsername() {
        return patient.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
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
