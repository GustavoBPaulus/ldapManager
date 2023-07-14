package br.edu.ifrs.ibiruba.ldapmanager.validators;

import java.util.Optional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import br.edu.ifrs.ibiruba.ldapmanager.dtos.AlunoDTO;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoCursoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoRepository;



 public class AlunoCursoValidator implements Validator {
	private AlunoCursoRepository alunoCursoRepository;
	private AlunoRepository alunoRepository;

	public AlunoCursoValidator(AlunoRepository alunoRepository, AlunoCursoRepository alunoCursoRepository) {
		this.alunoCursoRepository = alunoCursoRepository;
		this.alunoRepository = alunoRepository;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return AlunoCurso.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AlunoCurso alunoCurso = (AlunoCurso) target;

		Optional<AlunoCurso> alunoCursoFound = alunoCursoRepository.findByMatricula(alunoCurso.getMatricula());
		AlunoCurso alunoCursoFoundObject = new AlunoCurso();

		// matricula
		alunoCursoFound = alunoCursoRepository.findByMatricula(alunoCurso.getMatricula());
		if (alunoCursoFound.isPresent()) {
			alunoCursoFoundObject = alunoCursoFound.get();
			boolean foundIsEqualActual = alunoCursoFoundObject.getMatricula().equals(alunoCurso.getMatricula())
					&& alunoCursoFoundObject.getAluno().getLogin().equalsIgnoreCase(alunoCurso.getAluno().getLogin());
			if (!foundIsEqualActual) {
				errors.rejectValue("matricula", "validacao.alunocurso.matricula.existente");
			}
			
		}
		//aluno não existe
		try {
			Optional<Aluno> aluno = alunoRepository.findByLogin(alunoCurso.getAluno().getLogin());
			if (aluno.isEmpty())
				errors.rejectValue("aluno.login", "validacao.alunocurso.cpf.inexistente");
		}catch (Exception e){
			errors.rejectValue("aluno.login", "validacao.alunocurso.cpf.inexistente");
		}
		// matricula
		if (alunoCursoFound.isEmpty() && alunoCurso.getMatricula() == null) {
			errors.rejectValue("matricula", "validacao.alunocurso.matricula.vazia");
		}
	    
		//login
		if(alunoCursoFound.isEmpty() && alunoCurso.getAluno() == null) {
			errors.rejectValue("cpf", "validacao.servidor.login.vazio");
		}
		
	}
 }

