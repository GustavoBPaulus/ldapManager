package br.edu.ifrs.ibiruba.ldapmanager.services;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersisteServidorAuthenticatorComBaseNoServidorService {
    @Autowired
    ServidorCrudService servidorCrudService;
    @Autowired
    ServidorAuthenticatorService servidorAuthenticatorService;

    public boolean percorreListaDeServidoresEpersisteNaServidoresAuthenticator(){
        for (Servidor servidor : servidorCrudService.findAll()) {
            servidorAuthenticatorService.save(servidor);
        }

        return true;
    }
}
