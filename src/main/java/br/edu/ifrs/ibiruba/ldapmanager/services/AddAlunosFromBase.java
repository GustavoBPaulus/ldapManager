package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


import javax.naming.NamingException;

import br.edu.ifrs.ibiruba.ldapmanager.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoCursoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.MainAdCrud;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;

@Service
public class AddAlunosFromBase {

	@Autowired
	AlunoRepository alunoRepository;

	@Autowired
	AlunoCursoRepository alunoCursoRepository;
	public boolean addOrUpdateAluno(String matricula) {
		boolean success = true;
		//recuperar o servidor por login e persistir ou ajustar de acordo com o que tem na base
		Optional<AlunoCurso> alunoCursoRecuperadoOptional = alunoCursoRepository.findByMatricula(matricula);
		MainAdCrud mainAd = null;
		AlunoCurso alunoCursoRecuperado = null;
		if (alunoCursoRecuperadoOptional.isPresent()) {
			alunoCursoRecuperado = alunoCursoRecuperadoOptional.get();
			mainAd = new MainAdCrud(alunoCursoRecuperado.getMatricula());
			mainAd.newConnection();
			User user = mainAd.returnUser(alunoCursoRecuperado.getMatricula());
			//servidor já existe no ad então só atualiza
			if (user != null && (alunoCursoRecuperado.getMatricula().equalsIgnoreCase(user.getCn()))) {
				try {
					atualizaAlunoNoLdap(mainAd, alunoCursoRecuperado,  user);
				} catch (NamingException e) {
					//fazer aqui um tratamento de erro diferenciado
					throw new RuntimeException(e);
				}
			}
			//servidor não existe no ad
			else {
				persisteAlunoNoLdap(alunoCursoRecuperado);
			}

		}


		if (mainAd != null)
			mainAd.closeConnection();

		return success;
	}

	public boolean addLdapAlunosFromBase() throws NamingException {
		boolean succes = true;
		//List<AlunoCurso> listaDeAlunosCurso = alunoCursoRepository.findAll();
		//HashMap<String, List<AlunoCurso>> hashAlunosCurso = retornaHashDeAlunosCurso(listaDeAlunosCurso);
		// lista todos os alunos do integrado
		List<AlunoCurso> listaTodosAlunosIntegrado = alunoCursoRepository.findByTipoAluno("integrado");
		// lista todos os alunos do superior
		List<AlunoCurso> listaTodosAlunosSuperior = alunoCursoRepository.findByTipoAluno("superior");


		HashMap<String, List<AlunoCurso>> hashAlunosIntegrado = retornaHashDeAlunosCursoFiltrados(listaTodosAlunosIntegrado,"integrado");
		// hashMapDeAlunosSuperiorFiltrados
		HashMap<String, List<AlunoCurso>> hashAlunosSuperior = retornaHashDeAlunosCursoFiltrados(listaTodosAlunosSuperior, "superior");

		try {
			persisteAlunosLdapFromDatabase(hashAlunosIntegrado.get("naoCadastrados"), "integrado");
			persisteAlunosLdapFromDatabase(hashAlunosSuperior.get("naoCadastrados"), "superior");

			atualizaAlunosLdapFromDatabase(hashAlunosIntegrado.get("jaCadastrados"), "integrado");
			atualizaAlunosLdapFromDatabase(hashAlunosSuperior.get("jaCadastrados"), "superior");
		} catch (Exception e) {
			e.printStackTrace();
			succes = false;
		}

		return succes;

	}

	private HashMap<String, List<AlunoCurso>> retornaHashDeAlunosCurso(List<AlunoCurso> listaDeAlunosCurso) {
		HashMap<String, List<AlunoCurso>> hashAlunos = new HashMap<String, List<AlunoCurso>>();
		List<AlunoCurso> listaAlunosIntegrado = new ArrayList<AlunoCurso>();
		List<AlunoCurso> listaAlunosSuperior = new ArrayList<AlunoCurso>();

		String tipoDeAluno = "";
		// List<AlunoCurso> cursosAluno = aluno.getListaCursosAluno();
		for (AlunoCurso alunoCurso : listaDeAlunosCurso) {
			if (alunoCurso.getNomeCurso().equalsIgnoreCase("ELETROTÉCNICA")
					|| alunoCurso.getNomeCurso().equalsIgnoreCase("BACHAREL EM CIÊNCIA DA COMPUTAÇÃO")
					|| alunoCurso.getNomeCurso().equalsIgnoreCase("LICENCIATURA EM MATEMÁTICA")
					|| alunoCurso.getNomeCurso().equalsIgnoreCase("MECÂNICA")
					|| alunoCurso.getNomeCurso().equalsIgnoreCase("AGRONOMIA")
					|| alunoCurso.getNomeCurso().equalsIgnoreCase("ENGENHARIA MECÂNICA")) {

				listaAlunosSuperior.add(alunoCurso);

			} else  {
				
				listaAlunosIntegrado.add(alunoCurso);
			}
		}

		hashAlunos.put("integrado", listaAlunosIntegrado);
		hashAlunos.put("superior", listaAlunosSuperior);
		return hashAlunos;
	}

	private HashMap<String, List<AlunoCurso>> retornaHashDeAlunosCursoFiltrados(List<AlunoCurso> listaTodosAlunos,
			String tipoDeAluno) {
		List<AlunoCurso> listaAlunosJaCadastrados = new ArrayList<AlunoCurso>();
		List<AlunoCurso> listaAlunosNaoCadastrados = new ArrayList<AlunoCurso>();

		MainAdCrud mainAd = new MainAdCrud(tipoDeAluno);
		mainAd.newConnection();

		HashMap<String, User> hashMapAlunosFromAd = mainAd.returnUserHashMapUser();
		for (AlunoCurso alunoCurso : listaTodosAlunos) {
			// AlunoCurso ultimoCurso =
			// aluno.getListaCursosAluno().get(aluno.getListaCursosAluno().size() - 1);
			if (hashMapAlunosFromAd.get(alunoCurso.getMatricula()) != null)
				listaAlunosJaCadastrados.add(alunoCurso);
			else if (hashMapAlunosFromAd.get(alunoCurso.getMatricula()) == null)
				listaAlunosNaoCadastrados.add(alunoCurso);

		}
		HashMap<String, List<AlunoCurso>> hashAlunos = new HashMap<String, List<AlunoCurso>>();
		hashAlunos.put("jaCadastrados", listaAlunosJaCadastrados);
		hashAlunos.put("naoCadastrados", listaAlunosNaoCadastrados);
		return hashAlunos;
	}

	private void persisteAlunosLdapFromDatabase(List<AlunoCurso> listaDeAlunos, String tipoDeAluno) {

		for (AlunoCurso alunoCurso : listaDeAlunos) {
			MainAdCrud mainAd = new MainAdCrud(tipoDeAluno);
			mainAd.newConnection();
			// String[] nomeQuebradoNosEspacos = aluno.getNome_completo().split(" ");

			// //System.out.println("Senha do aluno: " + aluno.getSenha());

			User user = convertAlunoCursoToUser(alunoCurso);
			try {
				mainAd.addUser(user);
			} finally {
				mainAd.closeConnection();
			}

		}

	}

	private User persisteAlunoNoLdap(AlunoCurso alunoCurso) {
		MainAdCrud mainAd = new MainAdCrud(alunoCurso.getAluno().getLogin());
		mainAd.newConnection();


		User user = convertAlunoCursoToUser(alunoCurso);
		try {
			mainAd.addUser(user);
		} finally {
			mainAd.closeConnection();
		}

		return  user;

	}

	private void atualizaAlunoNoLdap(MainAdCrud mainAd, AlunoCurso alunoCurso,  User usuarioAd) throws NamingException {

		String[] vetorNomes = alunoCurso.getAluno().getNome_completo().split(" ");

		if (!usuarioAd.getGivenName().equalsIgnoreCase(alunoCurso.getAluno().getNome_completo()))
			atualizarAtributo(usuarioAd.getCn(), "givenName", alunoCurso.getAluno().getNome_completo(), mainAd);

		if ((!usuarioAd.getSamaccountname().equalsIgnoreCase(alunoCurso.getMatricula())))
			atualizarAtributo(usuarioAd.getCn(), "samAccountName", alunoCurso.getMatricula(), mainAd);

		if (!usuarioAd.getMail().equalsIgnoreCase(alunoCurso.getAluno().getEmail()) &&
				alunoCurso.getAluno().getEmail() != null)
			atualizarAtributo(usuarioAd.getCn(), "mail", alunoCurso.getAluno().getEmail(), mainAd);
		else if (alunoCurso.getAluno().getEmail() == null)
			atualizarAtributo(usuarioAd.getCn(), "mail", "sememail@sememail.com", mainAd);

		if (!usuarioAd.getSn().equalsIgnoreCase(alunoCurso.getAluno().getLogin())) {
			atualizarAtributo(usuarioAd.getCn(), "sn", alunoCurso.getAluno().getLogin(), mainAd);
		}
		boolean isAtivo = false;
		// verificar se o aluno está desabilitado

		if (alunoCurso.getStatusDiscente().equalsIgnoreCase("ATIVO")
				|| alunoCurso.getStatusDiscente().equalsIgnoreCase("FORMANDO"))
			isAtivo = true;

		if (!isAtivo) {
			int USER_ACCOUNT_DISABLED = 0x00000001;
			atualizarAtributo(usuarioAd.getCn(), "userAccountControl", Integer.toString(USER_ACCOUNT_DISABLED),
					mainAd);
			//System.out.println("aluno inativo: " + alunoCurso.getMatricula());
		}
	}

	private void atualizaAlunosLdapFromDatabase(List<AlunoCurso> listaDeAlunosCurso, String tipoDeAluno)
			throws NamingException {
		MainAdCrud mainAdConnection = new MainAdCrud(tipoDeAluno);
		mainAdConnection.newConnection();
		HashMap<String, User> hashMapAlunosFromAd = mainAdConnection.returnUserHashMapUser();
		mainAdConnection.closeConnection();

		for (int i = 0; i < listaDeAlunosCurso.size(); i++) {
			AlunoCurso alunoCursoFromDataBase = listaDeAlunosCurso.get(i);

			Aluno aluno = alunoRepository.getById(alunoCursoFromDataBase.getAluno().getLogin());
			MainAdCrud mainAd = new MainAdCrud(tipoDeAluno);
			mainAd.newConnection();
			// String[] vetorNomes = alunoFromApi.getNome_completo().split(" ");
			// List<AlunoCurso> listaDeCursosDoAluno = alunoFromApi.getListaCursosAluno();
			// AlunoCurso ultimoCursoDoAluno = null;
			/*
			 * for(AlunoCurso a : listaDeCursosDoAluno) {
			 * //if(a.getStatus_discente().equalsIgnoreCase("ATIVO") ||
			 * a.getStatus_discente().equalsIgnoreCase("FORMANDO")) ultimoCursoDoAluno = a;
			 * }
			 */

			User usuarioAd = hashMapAlunosFromAd.get(alunoCursoFromDataBase.getMatricula());

			if (!usuarioAd.getGivenName().equalsIgnoreCase(aluno.getNome_completo()))
				atualizarAtributo(usuarioAd.getCn(), "givenName", aluno.getNome_completo(), mainAd);

			if ((!usuarioAd.getSamaccountname().equalsIgnoreCase(alunoCursoFromDataBase.getMatricula())))
				atualizarAtributo(usuarioAd.getCn(), "samAccountName", alunoCursoFromDataBase.getMatricula(), mainAd);

			if (!usuarioAd.getMail().equalsIgnoreCase(aluno.getEmail()) && aluno.getEmail() != null)
				atualizarAtributo(usuarioAd.getCn(), "mail", aluno.getEmail(), mainAd);
			else if (aluno.getEmail() == null)
				atualizarAtributo(usuarioAd.getCn(), "mail", "sememail@sememail.com", mainAd);

			if (!usuarioAd.getSn().equalsIgnoreCase(aluno.getLogin())) {
				atualizarAtributo(usuarioAd.getCn(), "sn", aluno.getLogin(), mainAd);
			}
			boolean isAtivo = false;
			// verificar se o aluno está desabilitado

			if (alunoCursoFromDataBase.getStatusDiscente().equalsIgnoreCase("ATIVO")
					|| alunoCursoFromDataBase.getStatusDiscente().equalsIgnoreCase("FORMANDO"))
				isAtivo = true;

			if (!isAtivo) {
				int USER_ACCOUNT_DISABLED = 0x00000001;
				atualizarAtributo(usuarioAd.getCn(), "userAccountControl", Integer.toString(USER_ACCOUNT_DISABLED),
						mainAd);
				//System.out.println("aluno inativo: " + alunoCursoFromDataBase.getMatricula());
			}

			mainAd.closeConnection();
		}

	}

	private User convertAlunoCursoToUser(AlunoCurso alunoCurso) {
		Aluno aluno = alunoCurso.getAluno();

		User user = new User();

		user.setCn(alunoCurso.getMatricula());
		user.setGivenName(aluno.getNome_completo().toLowerCase());
		user.setMail(aluno.getEmail().toLowerCase());
		user.setName(aluno.getNome_completo().toLowerCase());
		user.setPassword(CriptografiaUtil.desencriptar(aluno.getSenha()));
		user.setSamaccountname(alunoCurso.getMatricula());
		user.setSn(aluno.getLogin());
		// user.setDisplayName(vetorNomes[0]+"."+vetorNomes[vetorNomes.length - 1]);
		return user;
	}

	private void atualizarAtributo(String userCN, String attribute, Object value, MainAdCrud mainAd)
			throws NamingException {
		mainAd.modifyAdAttribute(userCN, attribute.toLowerCase(), value);

	}
}
