package br.edu.ifrs.ibiruba.ldapmanager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.entities.UserDetailsImpl;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ServidorRepository servidorRepository;

    @Override
    public UserDetails loadUserByUsername(String cn) throws UsernameNotFoundException {
        Servidor servidor = servidorRepository.findBycn(cn)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new UserDetailsImpl(servidor);
    }
    
}
