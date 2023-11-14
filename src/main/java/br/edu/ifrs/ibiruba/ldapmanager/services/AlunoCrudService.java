package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifrs.ibiruba.ldapmanager.dtos.AlunoDTO;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.MessageModel;
import br.edu.ifrs.ibiruba.ldapmanager.exceptions.AlunoNaoEncontradoException;
import br.edu.ifrs.ibiruba.ldapmanager.exceptions.AlunoNaoPossuiCurso;
import br.edu.ifrs.ibiruba.ldapmanager.exceptions.MatriculaAtivaException;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoCursoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;
import br.edu.ifrs.ibiruba.ldapmanager.useful.SendMail;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
public class AlunoCrudService {

	@Autowired
	AlunoRepository alunoRepository;

	@Autowired
	AlunoCursoRepository alunoCursoRepository;

	@Autowired
	AddAlunosFromBase alunosFromBaseService;

	@Autowired
	ChangePasswordService changePasswordService;

	public List<Aluno> findAll() {

		return alunoRepository.findAll();
	}

	public List<String> retornaTiposDeAlunos() {

		return alunoRepository.listAllTiposDeAluno();
	}

	public List<String> retornaCursos() {

		return alunoRepository.listAllCursos();
	}

	public List<Aluno> findByNomeStatusAndTipoAluno(String nome, String status, String tipoDeAluno) {
		//System.out.println("cn: " + nome + " status: " + status);
		status = status.toUpperCase();
		// todos
		if (nome.equals("") && status.equalsIgnoreCase("todos") && tipoDeAluno.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if de todos ,findAll: cn vazio e status = todos ");

			return findAll();

		}
		// apenas por nome
		else if (!nome.equals("") && status.equalsIgnoreCase("todos") && tipoDeAluno.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if apenas pelo cn ,cn: " + nome + " status: " + status);

			return alunoRepository.findByNomeCompletoContaining(nome.trim().toUpperCase());

		} // apenas o status
		else if (nome.equals("") && !status.equalsIgnoreCase("todos") && tipoDeAluno.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if apenas pelo status ,cn: " + nome + " status: " + status);
			return alunoRepository.findByStatus(status);

		}
		// apenas pelo tipo de Aluno
		else if (nome.equals("") && status.equalsIgnoreCase("todos") && !tipoDeAluno.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if apenas pelo tipo ,cn: " + nome + " status: " + status);
			return alunoRepository.findByTipoAluno(tipoDeAluno.toLowerCase().trim());

		}

		// apenas nomeCompleto e status
		else if (!nome.equals("") && !status.equalsIgnoreCase("todos") && tipoDeAluno.equalsIgnoreCase("todos")) {
			//System.out.println("caiu no if apenas pelo cn e status ,cn: " + nome + " status: " + status);
			return alunoRepository.findByNomeCompletoContainingAndStatusEquals(nome.trim().toLowerCase(),status.toUpperCase().trim());

		}
		// apenas nomeCompleto e tipo
		else if (!nome.equals("") && status.equalsIgnoreCase("todos") && !tipoDeAluno.equalsIgnoreCase("todos")) {

			//System.out.println("if do cn e tipo, cn = " + nome + "status = " + status + "tipoDeAluno = " + tipoDeAluno);
			return alunoRepository.findByNomeCompletoContainingAndTipoAlunoEquals(nome, tipoDeAluno);

		}
		// apenas tipo e status
		else if (nome.equals("") && !status.equalsIgnoreCase("todos") && !tipoDeAluno.equalsIgnoreCase("todos")) {
			//System.out.println("if tipo e status, cn = " + nome + "status = " + status + "tipoDeAluno = " + tipoDeAluno);
			return alunoRepository.findByStatusEqualsAndTipoAlunoEquals(status.toUpperCase(), tipoDeAluno);
		}
		// tipo, status e cn
		else {
			//System.out.println("if cn, tipo e status preenchidos, cn = " + nome + "status = " + status+ "tipoDeAluno = " + tipoDeAluno);
			return alunoRepository.findByNomeCompletoContainingAndStatusEqualsAndTipoAlunoEquals(nome, status,
					tipoDeAluno);
		}
	}

	public Aluno findByCpf(String cpf) {
		return alunoRepository.findByLogin(cpf).orElseThrow(() -> new AlunoNaoEncontradoException(cpf));
	}
	/*
	 * public Aluno update(Aluno Aluno) {
	 * 
	 * 
	 * // Aluno.setSenha(CriptografiaUtil.encriptar(Aluno.getSenha()));
	 * 
	 * return AlunoRepository.save(Aluno); }
	 */

	public Aluno insert(@Valid AlunoDTO alunoDTO) {

		Aluno aluno = transformaAlunoDtoEmAluno(alunoDTO, new Aluno());

		AlunoCurso alunoCurso = transformaAlunoDtoEmAlunoCurso(alunoDTO, aluno, new AlunoCurso());

		List<AlunoCurso> listaAlunoCurso = new ArrayList<AlunoCurso>();
		listaAlunoCurso.add(alunoCurso);

		aluno.setNome_completo(aluno.getNomeCompleto().toUpperCase());
		alunoRepository.save(aluno);
		
		alunoCursoRepository.save(alunoCurso);
		aluno.setListaCursosAluno(listaAlunoCurso);
		
		alunoRepository.save(aluno);
		/*
		Optional<AlunoCurso> AlunoCursoOptional = alunoCursoRepository.findById(aluno.getLogin());

		List<AlunoCurso> listaDeCursos = new ArrayList<AlunoCurso>();

		// cadastra Aluno Curso
		//cadastraAluno(aluno, AlunoCursoOptional, listaDeCursos);
*/
		adicionaStatusDosCursosNaTabelaAluno();

		//adicionaAlunosDaBaseParaOldap();
		alunosFromBaseService.addOrUpdateAluno(alunoDTO.getMatricula());
		emailDeNovoUsuario(aluno);
		return aluno;
	}

	public Aluno edit(@Valid AlunoDTO alunoDTO) {
		//recupera aluno curso e aluno para editar
		Aluno alunoRecuperado = alunoRepository.findByLogin(alunoDTO.getLogin()).get();
		
		//Optional<AlunoCurso> optionalAlunoCursoRecuperado = alunoCursoRepository.findByMatricula(alunoDTO.getMatricula());
		
		//AlunoCurso alunoCursoRecuperado = optionalAlunoCursoRecuperado.get();
		
		//efetua as alterações feitas na tela de edição de aluno
		transformaAlunoDtoEmAluno(alunoDTO, alunoRecuperado);
		//transformaAlunoDtoEmAlunoCurso(alunoDTO, alunoRecuperado, alunoCursoRecuperado);
		
		//salva as alterações
		//alunoCursoRepository.save(alunoCursoRecuperado);
		alunoRepository.save(alunoRecuperado);

		adicionaStatusDosCursosNaTabelaAluno();

		alunosFromBaseService.addOrUpdateAluno(alunoDTO.getMatricula());

		
		return alunoRecuperado;
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

	private AlunoCurso transformaAlunoDtoEmAlunoCurso(AlunoDTO alunoDTO, Aluno aluno, AlunoCurso alunoCurso) {
		alunoCurso.setMatricula(alunoDTO.getMatricula());
		//System.out.println("matricula salva: "+alunoCurso.getMatricula());
		alunoCurso.setNomeCurso(alunoDTO.getNome_curso());
		alunoCurso.setStatusDiscente(alunoDTO.getStatus());
		//System.out.println("status discente: "+alunoCurso.getStatusDiscente());
		alunoCurso.setAluno(aluno);
		return alunoCurso;
	}

	private Aluno transformaAlunoDtoEmAluno(AlunoDTO alunoDTO, Aluno aluno) {
		aluno.setEmail(alunoDTO.getEmail());
		aluno.setLogin(alunoDTO.getLogin());
		aluno.setNome_completo(alunoDTO.getNomeCompleto());
		aluno.setSexo(aluno.getSexo());
		aluno.setStatus(aluno.getStatus());
		aluno.setData_nascimento(aluno.getData_nascimento());
		aluno.setStatus(alunoDTO.getStatus());
		aluno.setSenha(alunoDTO.getSenha());
		aluno.setTipoAluno(alunoDTO.getTipoAluno());
		return aluno;
	}
/*
	private void cadastraAluno(Aluno aluno, Optional<AlunoCurso> AlunoCursoOptional, List<AlunoCurso> listaDeCursos) {
		if (AlunoCursoOptional.isEmpty()) {
			AlunoCurso AlunoCurso = new AlunoCurso();

			// quando o cara chega, ainda não tem siape, então eu seto o cpf como matricula
			AlunoCurso.setMatricula(aluno.getLogin());

			// login
			AlunoCurso.setAluno(aluno);
			
			AlunoCurso.setStatus_discente("ATIVO");

			listaDeCursos.add(AlunoCurso);
			aluno.setListaCursosAluno(listaDeCursos);

			alunoCursoRepository.save(AlunoCurso);
			alunoRepository.save(aluno);
		}
	}
*/
	/*
	private void trataLogin(Aluno aluno) {
		aluno.setLogin(aluno.getLogin().toLowerCase());
		aluno.setLogin(aluno.getLogin().trim());
		aluno.setLogin(semAcento(aluno.getLogin()));

		aluno.setEmail(aluno.getEmail().toLowerCase().trim());
		aluno.setEmail(semAcento(aluno.getEmail()));

		aluno.setNome_completo(aluno.getNome_completo().toLowerCase().trim());

	}
*/	

	public void emailDeNovoUsuario(Aluno aluno) {
		List<AlunoCurso> listaDeCursosDoAluno = aluno.getListaCursosAluno();
		Iterator<AlunoCurso> listaDeCursoIterator = listaDeCursosDoAluno.iterator();
		while (listaDeCursoIterator.hasNext()) {
			AlunoCurso alunoCurso = listaDeCursoIterator.next();
			if (alunoCurso.getStatusDiscente().equalsIgnoreCase("ATIVO")
					|| alunoCurso.getStatusDiscente().equalsIgnoreCase("FORMANDO")) {
				MessageModel message = new MessageModel();

				message.setDestination(aluno.getEmail() + ",ti@ibiruba.ifrs.edu.br");
				message.setRemetent("timanager@ibiruba.ifrs.edu.br");
				message.setSubject("Novo usuário ifrs - campus ibirubá");
				message.setText("Seja bem vindo ao ifrs - campus ibirubá, seu usuário é: " + alunoCurso.getMatricula()
						+ " sua senha temporária é: " + CriptografiaUtil.desencriptar(aluno.getSenha())
						+ " esse usuário e senha devem ser utilizado para acessar os computadores dos laboratórios.");

				new SendMail().sendMailLogic(message);
			}
		}
	}

	public void deleteAlunoCurso(AlunoCurso alunoCurso) {

		if (alunoCurso.getStatusDiscente().equalsIgnoreCase("INATIVO")) {
			alunoCursoRepository.delete(alunoCurso);
			adicionaAlunosDaBaseParaOldap();
		} else
			throw new MatriculaAtivaException(alunoCurso.getMatricula());
	}

	public void deleteAluno(Aluno aluno) {

		if (aluno.getListaCursosAluno() == null || aluno.getListaCursosAluno().isEmpty()) {
			alunoRepository.delete(aluno);
			adicionaAlunosDaBaseParaOldap();
		} else {
			aluno.getListaCursosAluno().forEach(ac -> {
				deleteAlunoCurso(ac);
			});
		}

	}

	public void inativar(Aluno aluno) {
		List<AlunoCurso> listaDeAlunoCurso = alunoCursoRepository.findByAluno(aluno);

		if (listaDeAlunoCurso != null || !listaDeAlunoCurso.isEmpty()) {

			listaDeAlunoCurso.forEach(sc -> {
				sc.setStatusDiscente("INATIVO");
				alunoCursoRepository.save(sc);
			});

			adicionaStatusDosCursosNaTabelaAluno();
			adicionaAlunosDaBaseParaOldap();
		} else
			throw new AlunoNaoPossuiCurso(aluno.getLogin());
	}

	public void ativar(Aluno aluno) {
		List<AlunoCurso> listaDeAlunoCurso = alunoCursoRepository.findByAluno(aluno);

		if (listaDeAlunoCurso != null || !listaDeAlunoCurso.isEmpty()) {

			listaDeAlunoCurso.forEach(sc -> {
				sc.setStatusDiscente("ATIVO");
				alunoCursoRepository.save(sc);
			});

			adicionaStatusDosCursosNaTabelaAluno();
			adicionaAlunosDaBaseParaOldap();
		} else
			throw new AlunoNaoPossuiCurso(aluno.getLogin());
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

	public void setaStatusAlunoComBaseEmCursos(Aluno aluno) {
		List<AlunoCurso> CursosAlunoAtual = alunoCursoRepository.findByAluno(aluno);
		CursosAlunoAtual.forEach(c -> {
			if (c.getStatusDiscente().equalsIgnoreCase("ATIVO"))
				aluno.setStatus("ATIVO");
				alunoRepository.save(aluno);
		});
		if (aluno.getStatus() == null || !aluno.getStatus().equalsIgnoreCase("ATIVO"))
			aluno.setStatus("INATIVO");
			alunoRepository.save(aluno);
	}

	public void adicionaStatusDosCursosNaTabelaAluno() {

		List<Aluno> listaDeAlunos = findAll();

		listaDeAlunos.forEach(s -> {
			setaStatusAlunoComBaseEmCursos(s);
			alunoRepository.save(s);
		});
	}

	public static String semAcento(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	public AlunoDTO converteAlunoParaAlunoDTO(Aluno aluno) {
		// TODO Auto-generated method stub
		AlunoDTO alunoDTO = new AlunoDTO();
		alunoDTO.setEmail(aluno.getEmail());
		alunoDTO.setLogin(aluno.getLogin());
		alunoDTO.setNomeCompleto(aluno.getNome_completo());
		alunoDTO.setSenha(aluno.getSenha());
		alunoDTO.setSexo(aluno.getSexo());
		alunoDTO.setStatus(aluno.getStatus());
		alunoDTO.setTipoAluno(aluno.getTipoAluno());
		
		//pegar o ultimo aluno curso cadastrado
		AlunoCurso ultimoAlunoCurso = aluno.getListaCursosAluno().get(aluno.getListaCursosAluno().size() - 1);
		alunoDTO.setMatricula(ultimoAlunoCurso.getMatricula());
		alunoDTO.setNome_curso(ultimoAlunoCurso.getNomeCurso());
		
		
		return alunoDTO;
	}

}
