package br.edu.ifrs.ibiruba.ldapmanager.entities;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;



public class UserDetailsImpl implements UserDetails {

    private Servidor servidor;

    public UserDetailsImpl(Servidor servidor) {
        this.servidor = servidor;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        

        return AuthorityUtils.createAuthorityList("TI");
    }

    @Override
    public String getPassword() {
        return servidor.getSenha();
    }

    @Override
    public String getUsername() {
        return servidor.getCn();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }
    
}
