package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.InvalidAttributeValueException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.entities.ServidorCargo;
import br.edu.ifrs.ibiruba.ldapmanager.entities.User;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.MainAdCrud;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;


@Service
public class AddServidoresFromBase {
	@Autowired
	ServidorRepository servidorRepository;


	public boolean addOrUpdateServidor(String login) {
		boolean success = true;
		//recuperar o servidor por login e persistir ou ajustar de acordo com o que tem na base
		Optional<Servidor> servidorRecuperadoOptional = servidorRepository.findByLogin(login);
		MainAdCrud mainAd = null;
		Servidor servidorRecuperado = null;
		if (servidorRecuperadoOptional.isPresent()) {
			servidorRecuperado = servidorRecuperadoOptional.get();
			mainAd = new MainAdCrud(servidorRecuperado.getTipoServidor());
			mainAd.newConnection();
			User user = mainAd.returnUser(servidorRecuperado.getCn());
			//servidor já existe no ad então só atualiza
			if (user != null && (servidorRecuperado.getCn().equalsIgnoreCase(user.getCn()))) {
				try {
					atualizaServidorNoLdap(mainAd, servidorRecuperado,  user);
				} catch (NamingException e) {
					//fazer aqui um tratamento de erro diferenciado
					throw new RuntimeException(e);
				}
			}
			//servidor não existe no ad
			else {
				persisteServidorNoLdap(servidorRecuperado);
			}

		}


		if (mainAd != null)
			mainAd.closeConnection();

		return success;
	}

	public boolean sincronizarServidoresFromBase() throws InvalidAttributeValueException, NameAlreadyBoundException {
		boolean success = true;

		// lista todos os servidores docentes
		List<Servidor> listaTodosServidoresDocentes = servidorRepository.findByTipoServidor("docente");
		// lista todos os servidores taes
		List<Servidor> listaTodosServidoresTaes = servidorRepository.findByTipoServidor("tae");
		////System.out.println("usuário teste.teste existe na lista de servidores: "+ listaTodosServidoresTaes.contains("teste.teste"));
		//lista todos os servidores terceirizados
		List<Servidor> listaTodosServidoresTerceirizados = servidorRepository.findByTipoServidor("terceirizado");

		// hashMapDeServidors docentes filtrados
		HashMap<String, List<Servidor>> hashServidoresDocentes = retornaHashDeServidorsFiltrados(
				listaTodosServidoresDocentes, "docente");
		// hashMapDeServidorsSuperiorFiltrados
		HashMap<String, List<Servidor>> hashServidoresTaes = retornaHashDeServidorsFiltrados(listaTodosServidoresTaes,
				"tae");
		//hashMapDeServidoresTerceirizadosFiltrados
		HashMap<String, List<Servidor>> hashServidoresTerceirizados = retornaHashDeServidorsFiltrados(listaTodosServidoresTerceirizados,
				"terceirizado");
		//System.out.println("tamanho do hash de terceirizados: " + hashServidoresTerceirizados.size());


		persisteServidoresFromBase(hashServidoresDocentes.get("naoCadastrados"), "docente");


		persisteServidoresFromBase(hashServidoresTaes.get("naoCadastrados"), "tae");


		persisteServidoresFromBase(hashServidoresTerceirizados.get("naoCadastrados"), "terceirizado");

		try {
			atualizaServidoresLdapFromDatabase(hashServidoresDocentes.get("jaCadastrados"), "docente");
		} catch (NamingException e) {
			success = false;
			e.printStackTrace();
		}
		try {
			atualizaServidoresLdapFromDatabase(hashServidoresTaes.get("jaCadastrados"), "tae");
		} catch (NamingException e) {
			success = false;
			e.printStackTrace();
		}

		try {
			atualizaServidoresLdapFromDatabase(hashServidoresTerceirizados.get("jaCadastrados"), "terceirizado");
		} catch (NamingException e) {
			success = false;
			e.printStackTrace();
		}

		return success;
	}


	private HashMap<String, List<Servidor>> retornaHashDeServidorsFiltrados(List<Servidor> listaTodosServidors,
																			String tipoDeServidor) {
		List<Servidor> listaServidoresJaCadastrados = new ArrayList<Servidor>();
		List<Servidor> listaServidoresNaoCadastrados = new ArrayList<Servidor>();

		MainAdCrud mainAd = new MainAdCrud(tipoDeServidor);
		mainAd.newConnection();
		HashMap<String, User> hashMapServidorsFromAd = mainAd.returnUserHashMapUser();
		for (Servidor servidor : listaTodosServidors) {

			if (hashMapServidorsFromAd.get(servidor.getCn().toLowerCase()) != null) {
				listaServidoresJaCadastrados.add(servidor);

			} else if (hashMapServidorsFromAd.get(servidor.getCn().toLowerCase()) == null) {
				listaServidoresNaoCadastrados.add(servidor);

			}

		}
		HashMap<String, List<Servidor>> hashServidors = new HashMap<String, List<Servidor>>();
		hashServidors.put("jaCadastrados", listaServidoresJaCadastrados);
		hashServidors.put("naoCadastrados", listaServidoresNaoCadastrados);

		mainAd.closeConnection();
		return hashServidors;
	}

	private void persisteServidoresFromBase(List<Servidor> listaDeServidors, String tipoDeServidor)
			throws javax.naming.directory.InvalidAttributeValueException, javax.naming.NameAlreadyBoundException {

		for (Servidor servidor : listaDeServidors) {
			persisteServidorNoLdap(servidor);
		}
	}

	private User persisteServidorNoLdap(Servidor servidor) {
		MainAdCrud mainAd = new MainAdCrud(servidor.getTipoServidor());
		mainAd.newConnection();
		//String[] nomeQuebradoNosEspacos = servidor.getNome_completo().split(" ");
		//System.out.println("servidor para ser persistido: " + servidor.getCn());
		//System.out.println("Senha do servidor: " + servidor.getSenha());

		User user = convertServidorToUser(servidor);
		try {
			mainAd.addUser(user);
		} finally {
			mainAd.closeConnection();
		}

	return  user;

}




	private User convertServidorToUser(Servidor servidor) {
		String[] vetorNomes = servidor.getNome_completo().split(" ");
		User user = new User();

		user.setCn(servidor.getCn().toLowerCase());
		user.setGivenName(vetorNomes[0].toLowerCase());
		if (servidor.getEmail() != null)
			user.setMail(servidor.getEmail().toLowerCase());
		user.setName(servidor.getCn());
		//System.out.println("Servidor: " + servidor.getCn() + " password: " + servidor.getSenha());
		user.setPassword(CriptografiaUtil.desencriptar(servidor.getSenha()));
		user.setSamaccountname(servidor.getCn().toLowerCase());
		user.setSn(servidor.getCn().toLowerCase());

		return user;
	}

	private void atualizaServidoresLdapFromDatabase(List<Servidor> listaDeServidors, String tipoDeServidor)
			throws NamingException {
		MainAdCrud mainAdConnection = new MainAdCrud(tipoDeServidor);
		mainAdConnection.newConnection();
		HashMap<String, User> hashMapServidorsFromAd = mainAdConnection.returnUserHashMapUser();
		for (int i = 0; i < listaDeServidors.size(); i++) {
			Servidor servidorFromBase = listaDeServidors.get(i);
			User usuarioAd = hashMapServidorsFromAd.get(servidorFromBase.getCn().toLowerCase());
			atualizaServidorNoLdap(mainAdConnection,servidorFromBase,   usuarioAd);
		}
		mainAdConnection.closeConnection();
	}

	private void atualizaServidorNoLdap(MainAdCrud mainAd, Servidor servidorFromBase,  User usuarioAd) throws NamingException {

		String[] vetorNomes = servidorFromBase.getNome_completo().split(" ");
		if (!usuarioAd.getGivenName().equalsIgnoreCase(vetorNomes[0]))
			atualizarAtributo(usuarioAd.getCn(), "givenName", vetorNomes[0], mainAd);

		//if ((!usuarioAd.getSamaccountname().equalsIgnoreCase(servidorFromBase.getCn())))
		atualizarAtributo(usuarioAd.getCn(), "samAccountName", servidorFromBase.getCn(), mainAd);

		if (!usuarioAd.getMail().equalsIgnoreCase(servidorFromBase.getEmail()))
			atualizarAtributo(usuarioAd.getCn(), "mail", servidorFromBase.getEmail(), mainAd);

		if (!usuarioAd.getSn().equalsIgnoreCase(vetorNomes[vetorNomes.length - 1])) {
			atualizarAtributo(usuarioAd.getCn(), "sn", servidorFromBase.getNome_completo()
					.split(" ")[servidorFromBase.getNome_completo().split(" ").length - 1], mainAd);
		}
		boolean isAtivo = false;
		// verificar se o servidor está desabilitado

		for (ServidorCargo servidorCargoModelo : servidorFromBase.getListaCargos()) {
			if (servidorCargoModelo.getStatus().equalsIgnoreCase("ATIVO"))
				isAtivo = true;
		}

		if (!isAtivo) {
			//System.out.println("USUÁRIO ESTÁ INATIVO: " + servidorFromBase.getCn());

			mainAd.disableAccount(servidorFromBase.getCn());

		}
		if (isAtivo) {
			//System.out.println("USUÁRIO ESTÁ ATIVO: " + servidorFromBase.getCn());

			mainAd.enableAccount(servidorFromBase.getCn());

		}
	}

	private void atualizarAtributo(String userCN, String attribute, Object value, MainAdCrud mainAd)
			throws NamingException {
		mainAd.modifyAdAttribute(userCN, attribute.toLowerCase(), value);

	}

	public void exluirUsuariosQueExistemNaBaseEnaoExistemNoLdap(){

		List<User> listaDeTaesQueExistemNoLdapEnaoNaBase= retornaListaDeServidoresQueExistemNoAdEnaoExistemNaBase("tae");
		excluirListaDeUsuariosQueExistemNoLdapEnaoExistemNaBase(listaDeTaesQueExistemNoLdapEnaoNaBase, "tae");

		List<User> listaDeDocentesQueExistemNoLdapEnaoNaBase= retornaListaDeServidoresQueExistemNoAdEnaoExistemNaBase("docente");
		excluirListaDeUsuariosQueExistemNoLdapEnaoExistemNaBase(listaDeDocentesQueExistemNoLdapEnaoNaBase, "docente");

		//por enquanto estamos excluindo só servidores, se decidir fazer para alunos tem que fazer os métodos de retorno
		List<User> listaDeTerceirizadosQueNaoExistemNaBaseEexistemNoLdap = retornaListaDeServidoresQueExistemNoAdEnaoExistemNaBase("terceirizado");
		excluirListaDeUsuariosQueExistemNoLdapEnaoExistemNaBase(listaDeTerceirizadosQueNaoExistemNaBaseEexistemNoLdap, "terceirizado");
	}

	private static void excluirListaDeUsuariosQueExistemNoLdapEnaoExistemNaBase(List<User> listaDeUsuariosQueNaoExistemNaBaseEexistemNoLdap, String tipoUsuario) {
		MainAdCrud mainAd = new MainAdCrud(tipoUsuario);
		mainAd.newConnection();
		listaDeUsuariosQueNaoExistemNaBaseEexistemNoLdap.forEach(user ->{
			try {
				mainAd.deleteUser(user);
			} catch (NamingException e) {
				throw new RuntimeException(e);
			}
		});
		mainAd.closeConnection();
	}

	private List<User> retornaListaDeServidoresQueExistemNoAdEnaoExistemNaBase(String tipoServidor) {
		List<User> listaDeUsuariosQueNaoExistemNaBaseEexistemNoLdap = new ArrayList<User>();

		MainAdCrud mainAd = new MainAdCrud(tipoServidor);
		mainAd.newConnection();
		HashMap<String, User> hashMapServidorsFromAd = mainAd.returnUserHashMapUser();
		hashMapServidorsFromAd.keySet().forEach(
				cn -> {
					Optional<Servidor> servidoresRecuperado = servidorRepository.findByCn(cn);
					if (servidoresRecuperado.isEmpty())
						listaDeUsuariosQueNaoExistemNaBaseEexistemNoLdap.add(hashMapServidorsFromAd.get(cn));
				});
		mainAd.closeConnection();
		return  listaDeUsuariosQueNaoExistemNaBaseEexistemNoLdap;
	}



	
}
