package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.util.List;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.MessageModel;
import br.edu.ifrs.ibiruba.ldapmanager.exceptions.AlunoNaoEncontradoException;
import br.edu.ifrs.ibiruba.ldapmanager.exceptions.MatriculaAtivaException;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoCursoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;
import br.edu.ifrs.ibiruba.ldapmanager.useful.SendMail;


@Service
public class AlunoCursoService {

	@Autowired
	AlunoRepository alunoRepository;
	
	@Autowired
	AlunoCrudService alunoCrudService;

	@Autowired
	AlunoCursoRepository alunoCursoRepository;

	@Autowired
	AddAlunosFromBase alunosFromBaseService;

	@Autowired
	ChangePasswordService changePasswordService;

	public List<AlunoCurso> findAll() {

		return alunoCursoRepository.findAll();
	}

	public List<String> retornaTiposDeAlunos() {

		return alunoRepository.listAllTiposDeAluno();
	}

	public List<String> retornaCursos() {

		return alunoRepository.listAllCursos();
	}

	public List<AlunoCurso> findByNomeStatusAndCurso(String nome, String status, String curso) {
		//System.out.println("cn: " + nome + " status: " + status + " curso: "+curso);
		status = status.toUpperCase();
		nome = nome.toUpperCase();
		// todos
		if (nome.equals("") && status.equalsIgnoreCase("todos") && curso.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if de todos ,findAll: cn vazio e status = todos ");

			return findAll();

		}
		// apenas por nome
		else if (!nome.equals("") && status.equalsIgnoreCase("todos") && curso.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if apenas pelo cn ,cn: " + nome + " status: " + status);

			return alunoCursoRepository.findByAlunoNomeCompletoContaining(nome.trim().toUpperCase());

		}
		// apenas o status
		else if (nome.equals("") && !status.equalsIgnoreCase("todos") && curso.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if apenas pelo status ,cn: " + nome + " status: " + status);
			return alunoCursoRepository.findByStatusDiscente(status);

		}
		// apenas pelo curso de Aluno
		else if (nome.equals("") && status.equalsIgnoreCase("todos") && !curso.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if apenas pelo nome do curso: " + nome + " status: " + status);
			return alunoCursoRepository.findBynomeCurso(curso.toUpperCase().trim());

		}

		// apenas nomeCompleto e status
		else if (!nome.equals("") && !status.equalsIgnoreCase("todos") && curso.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if apenas pelo cn e status ,cn: " + nome + " status: " + status);
			return alunoCursoRepository.findByAlunoNomeCompletoContainingAndStatusDiscenteEquals(nome.trim().toLowerCase(),
					status.toUpperCase().trim());

		}
		// apenas nomeCompleto e curso
		else if (!nome.equals("") && status.equalsIgnoreCase("todos") && !curso.equalsIgnoreCase("todos")) {

			//System.out.println("if do cn e tipo, cn = " + nome + "status = " + status + "curso = " + curso);
			return alunoCursoRepository.findByAlunoNomeCompletoContainingAndNomeCursoEquals(nome, curso);

		}
		// apenas tipo e status
		else if (nome.equals("") && !status.equalsIgnoreCase("todos") && !curso.equalsIgnoreCase("todos")) {
			//System.out.println("if tipo e status, cn = " + nome + "status = " + status + "tipoDeAluno = " + curso);
			return alunoCursoRepository.findByStatusDiscenteEqualsAndNomeCursoEquals(status.toUpperCase(), curso);
		}
		// tipo, status e cn
		else {
			//System.out.println("if cn, tipo e status preenchidos, cn = " + nome + "status = " + status+ "curso = " + curso);
			return alunoCursoRepository.findByAlunoNomeCompletoContainingAndStatusDiscenteEqualsAndNomeCursoEquals(
					nome, status, curso);
		}
	}

	public AlunoCurso findByMatricula(String matricula) {
		return alunoCursoRepository.findByMatricula(matricula).orElseThrow(() -> new AlunoNaoEncontradoException(matricula));
	}

	public AlunoCurso insert(@Valid AlunoCurso alunoCurso) {

		alunoCursoRepository.save(alunoCurso);

		alunoCrudService.adicionaStatusDosCursosNaTabelaAluno();

		adicionaAlunosDaBaseParaOldap();

		emailDeNovoUsuário(alunoCurso);
		return alunoCurso;
	}

	public AlunoCurso edit(@Valid AlunoCurso alunoCurso) {
				alunoCursoRepository.save(alunoCurso);

				alunoCrudService.adicionaStatusDosCursosNaTabelaAluno();

		adicionaAlunosDaBaseParaOldap();

		return alunoCurso;
	}

	private void adicionaAlunosDaBaseParaOldap() {
		try {
			alunosFromBaseService.addLdapAlunosFromBase();
		} catch (InvalidAttributeValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NameAlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public void emailDeNovoUsuário(AlunoCurso alunoCurso) {
			if (alunoCurso.getStatusDiscente().equalsIgnoreCase("ATIVO")
					|| alunoCurso.getStatusDiscente().equalsIgnoreCase("FORMANDO")) {
				MessageModel message = new MessageModel();

				message.setDestination(alunoCurso.getAluno().getEmail() + ",ti@ibiruba.ifrs.edu.br");
				message.setRemetent("timanager@ibiruba.ifrs.edu.br");
				message.setSubject("Novo usuário ifrs - campus ibirubá");
				message.setText("Seja bem vindo ao ifrs - campus ibirubá, seu usuário é: " + alunoCurso.getMatricula()
						+ " sua senha temporária é: " + CriptografiaUtil.desencriptar(alunoCurso.getAluno().getSenha())
						+ " esse usuário e senha devem ser utilizado para acessar os computadores dos laboratórios.");

				new SendMail().sendMailLogic(message);
			}
		}
	

	public void deleteAlunoCurso(AlunoCurso alunoCurso) {

		if (alunoCurso.getStatusDiscente().equalsIgnoreCase("INATIVO")) {
			alunoCursoRepository.delete(alunoCurso);
			adicionaAlunosDaBaseParaOldap();
		} else
			throw new MatriculaAtivaException(alunoCurso.getMatricula());
	}



	public void inativar(AlunoCurso alunoCurso) {
	
				alunoCurso.setStatusDiscente("INATIVO");
				alunoCursoRepository.save(alunoCurso);
				alunoCrudService.adicionaStatusDosCursosNaTabelaAluno();
			adicionaAlunosDaBaseParaOldap();
		
	}

	public void ativar(AlunoCurso alunoCurso) {
				alunoCurso.setStatusDiscente("ATIVO");
				alunoCursoRepository.save(alunoCurso);
	
				alunoCrudService.adicionaStatusDosCursosNaTabelaAluno();
			adicionaAlunosDaBaseParaOldap();
		}

	public boolean resetarSenhaAluno(Aluno aluno, AlunoCurso alunoCurso) {

		String senhaReset = alunoCurso.getMatricula() + "@ibiruba.ifrs".trim();
		AlterPasswordModel alterPasswordModel = new AlterPasswordModel();
		alterPasswordModel.setActualPassword(CriptografiaUtil.desencriptar(aluno.getSenha()));
		alterPasswordModel.setNewPassword(senhaReset);
		alterPasswordModel.setTypeOfUser("Aluno");
		alterPasswordModel.setUser(alunoCurso.getMatricula());

		return changePasswordService.alterPassword(alterPasswordModel);

	}


    public List<AlunoCurso> findByAluno(Aluno aluno) {

		return alunoCursoRepository.findByAluno(aluno);
    }
}
