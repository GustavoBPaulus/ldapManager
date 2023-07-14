package br.edu.ifrs.ibiruba.ldapmanager.validators;

import java.util.Optional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import br.edu.ifrs.ibiruba.ldapmanager.dtos.AlunoDTO;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoCursoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoRepository;

public class AlunoDtoValidator implements Validator {

	private AlunoRepository alunoRepository;
	private AlunoCursoRepository alunoCursoRepository;

	public AlunoDtoValidator(AlunoRepository alunoRepository, AlunoCursoRepository alunoCursoRepository) {
		this.alunoRepository = alunoRepository;
		this.alunoCursoRepository = alunoCursoRepository;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return AlunoDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AlunoDTO alunoDto = (AlunoDTO) target;

		Optional<Aluno> alunoFound = alunoRepository.findByEmail(alunoDto.getEmail());
		Aluno alunoFoundObject = new Aluno();

		// login
		alunoFound = alunoRepository.findByLogin(alunoDto.getLogin());
		if (alunoFound.isPresent()) {
			alunoFoundObject = alunoFound.get();
			boolean foundIsEqualActual = (alunoDto.getLogin().equalsIgnoreCase(alunoFoundObject.getLogin())
					&& (alunoDto.getEmail().equalsIgnoreCase(alunoFoundObject.getEmail())) ? true : false);
			if (!foundIsEqualActual) {
				errors.rejectValue("login", "validacao.aluno.login.existente");
			}
			if (alunoDto.getLogin() == null || alunoDto.getLogin().equals("")) {
				errors.rejectValue("login", "validacao.aluno.login.vazio");
			}
			if (!alunoDto.getLogin().equals(alunoFoundObject.getLogin())) {
				errors.rejectValue("login", "validacao.Aluno.login.naopodeseralterado");
			}
		}

		// matricula
		//esse if sempre precisa ficar depois do validate de login para pegar no alunoFound o resultado do findLogin	
		//só vai validar se for um novo cadastro
		if ( !alunoFound.isPresent() && (alunoDto.getMatricula() == null || alunoDto.getMatricula().equals(""))) {
			errors.rejectValue("matricula", "validacao.alunocurso.matricula.vazia");
		}
		// curso, esse if também precisa ficar nessa posição do código, só vai validar se for um novo cadastro
				if (!alunoFound.isPresent() && (alunoDto.getNome_curso() == null || alunoDto.getNome_curso().equals(""))) {
					errors.rejectValue("curso", "validacao.aluno.curso.vazio");
				}
		AlunoCurso alunoCursoFoundObject;
				Optional<AlunoCurso> alunoCursoFound = alunoCursoRepository.findByMatricula(alunoDto.getMatricula());
				if (alunoCursoFound.isPresent()) {
					alunoCursoFoundObject = alunoCursoFound.get();
					boolean foundIsEqualActual = (alunoDto.getMatricula()).equalsIgnoreCase(alunoCursoFoundObject.getMatricula())
							&& (alunoDto.getLogin().equalsIgnoreCase(alunoCursoFoundObject.getAluno().getLogin())) ? true : false;
					if (!foundIsEqualActual) {
						errors.rejectValue("matricula", "validacao.alunocurso.matricula.existente");
					}
				}
		
		// email
		alunoFound = alunoRepository.findByEmail(alunoDto.getEmail());
		if (alunoFound.isPresent()) {
			alunoFoundObject = alunoFound.get();
			boolean foundIsEqualActual = (alunoDto.getLogin().equalsIgnoreCase(alunoFoundObject.getLogin())
					&& (alunoDto.getEmail().equalsIgnoreCase(alunoFoundObject.getEmail())) ? true : false);
			if (!foundIsEqualActual) {
				errors.rejectValue("email", "validacao.aluno.email.existente");
			}
		}

		if (alunoDto.getEmail() == null || alunoDto.getEmail().equals("")) {
			errors.rejectValue("email", "validacao.aluno.email.vazio");
		}

		// nome
		alunoFound = alunoRepository.findByNomeCompleto(alunoDto.getNomeCompleto());
		if (alunoFound.isPresent()) {
			alunoFoundObject = alunoFound.get();
			boolean foundIsEqualActual = (alunoDto.getLogin().equalsIgnoreCase(alunoFoundObject.getLogin())
					&& (alunoDto.getEmail().equalsIgnoreCase(alunoFoundObject.getEmail())) ? true : false);
			if (!foundIsEqualActual) {
				errors.rejectValue("email", "validacao.aluno.email.existente");
			}
		}

		if (alunoDto.getEmail() == null || alunoDto.getEmail().equals("")) {
			errors.rejectValue("email", "validacao.aluno.email.vazio");
		}

		// tipo
		/*
		if (alunoDto.getTipoAluno() == null || alunoDto.getTipoAluno().equals("")) {
			errors.rejectValue("tipo", "validacao.aluno.tipo.vazio");
		}
*/
		// status
		if (alunoDto.getStatus() == null || alunoDto.getStatus().equals("")) {
			errors.rejectValue("status", "validacao.aluno.status.vazio");
		}

		
	


	}

}
