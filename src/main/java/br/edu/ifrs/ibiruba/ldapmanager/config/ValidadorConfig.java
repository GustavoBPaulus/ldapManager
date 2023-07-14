package br.edu.ifrs.ibiruba.ldapmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoCursoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;
import br.edu.ifrs.ibiruba.ldapmanager.validators.AlunoCursoValidator;
import br.edu.ifrs.ibiruba.ldapmanager.validators.AlunoDtoValidator;
import br.edu.ifrs.ibiruba.ldapmanager.validators.ServidorValidator;





@Configuration
public class ValidadorConfig {

    @Autowired
    private ServidorRepository servidorRepository;

    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private AlunoCursoRepository alunoCursoRepository;
    
    

    @Bean
    public ServidorValidator servidorValidator() {
        return new ServidorValidator(servidorRepository);
    }

    @Bean
   public AlunoDtoValidator alunoValidator() {
    	return new AlunoDtoValidator(alunoRepository, alunoCursoRepository);
   }
    
    @Bean
    public AlunoCursoValidator alunoCursoValidator() {
    	return new AlunoCursoValidator(alunoRepository, alunoCursoRepository);
    }
    
    
    
}
