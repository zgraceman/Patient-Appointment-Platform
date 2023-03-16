package org.perscholas.mbs.security;

import org.perscholas.mbs.dao.AuthGroupRepoI;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    PatientRepoI patientRepoI;
    AuthGroupRepoI authGroupRepoI;

    @Autowired
    public MyUserDetailsService(PatientRepoI patientRepoI, AuthGroupRepoI authGroupRepoI) {
        this.patientRepoI = patientRepoI;
        this.authGroupRepoI = authGroupRepoI;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return new MyUserPrincipal
                (patientRepoI.findByEmailAllIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found, " + username)),
                        authGroupRepoI.findByEmail(username));
    }
}
