package br.edu.ifrs.ibiruba.ldapmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;
import br.edu.ifrs.ibiruba.ldapmanager.validators.ServidorValidator;





@Configuration
public class ValidadorConfig {

    @Autowired
    private ServidorRepository servidorRepository;


    @Bean
    public ServidorValidator servidorValidator() {
        return new ServidorValidator(servidorRepository);
    }

   

}
