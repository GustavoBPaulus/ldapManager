package br.edu.ifrs.ibiruba.ldapmanager.services;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.entities.ServidorAuthenticator;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorAuthenticatorRepository;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServidorAuthenticatorService {
    @Autowired
    ServidorAuthenticatorRepository servidorAuthenticatorRepository;
    public void save(Servidor servidor){
        ServidorAuthenticator servidorAuthenticator = new ServidorAuthenticator();
        servidorAuthenticator.setCn(servidor.getCn());
        servidorAuthenticator.setNome(servidor.getNome_completo());
        String senhaDesencriptada = new CriptografiaUtil().desencriptar(servidor.getSenha());
        String senhaEncriptadaBcrypt = new BCryptPasswordEncoder().encode(senhaDesencriptada);
        servidorAuthenticator.setSenhaBcrypt(senhaEncriptadaBcrypt);
        servidorAuthenticator.setStatus(servidor.getStatus());

        servidorAuthenticatorRepository.save(servidorAuthenticator);
    }

    public boolean delete(Servidor servidor){
        Optional<ServidorAuthenticator> servidorAuthenticator = servidorAuthenticatorRepository.findById(servidor.getCn());
           if(servidorAuthenticator.isPresent()) {
               servidorAuthenticatorRepository.delete(servidorAuthenticator.get());
           return  true;
           }
            return false;
         }


}
