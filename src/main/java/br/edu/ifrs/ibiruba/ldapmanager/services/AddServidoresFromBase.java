package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	public boolean addServidorsFromBase() throws InvalidAttributeValueException, NameAlreadyBoundException {
		boolean success = true;
		List<Servidor> listaDeServidores = servidorRepository.findAll();
		
		for(Servidor servidor : listaDeServidores) {
			if(servidor.getTipoServidor().equalsIgnoreCase("terceirizado"))
				System.out.println("servidor do tipo terceirizado: " +servidor.getCn());
		}
		
		
		//System.out.println("teste.teste sendo recuperado: "+listaDeServidores.);
		HashMap<String, List<Servidor>> hashServidores = transformaListaServidoresEmHash(listaDeServidores);
		//System.out.println("usuário teste.teste existe no hashmap geral: "+ hashServidores.containsKey("teste.teste"));
		
		// lista todos os servidores docentes
		List<Servidor> listaTodosServidoresDocentes = hashServidores.get("docente");
		// lista todos os servidores taes
		List<Servidor> listaTodosServidoresTaes = hashServidores.get("tae");
		//System.out.println("usuário teste.teste existe na lista de servidores: "+ listaTodosServidoresTaes.contains("teste.teste"));
		//lista todos os servidores terceirizados
		List<Servidor> listaTodosServidoresTerceirizados = hashServidores.get("terceirizado");
		
		
		
		// hashMapDeServidors docentes filtrados
		HashMap<String, List<Servidor>> hashServidoresDocentes = retornaHashDeServidorsFiltrados(
				listaTodosServidoresDocentes, "docente");
		// hashMapDeServidorsSuperiorFiltrados
		HashMap<String, List<Servidor>> hashServidoresTaes = retornaHashDeServidorsFiltrados(listaTodosServidoresTaes,
				"tae");
		//hashMapDeServidoresTerceirizadosFiltrados
		HashMap<String, List<Servidor>> hashServidoresTerceirizados = retornaHashDeServidorsFiltrados(listaTodosServidoresTerceirizados,
				"terceirizado");
		System.out.println("tamanho do hash de terceirizados: " +hashServidoresTerceirizados.size());
		
		
		persisteServidoresFromReitoria(hashServidoresDocentes.get("naoCadastrados"), "docente");

		
		
		persisteServidoresFromReitoria(hashServidoresTaes.get("naoCadastrados"), "tae");
		
		
		persisteServidoresFromReitoria(hashServidoresTerceirizados.get("naoCadastrados"), "terceirizado");
		
		try {
			atualizaServidorsFromReitoria(hashServidoresDocentes.get("jaCadastrados"), "docente");
		} catch (NamingException e) {
			success = false;
			e.printStackTrace();
		}
		try {
			atualizaServidorsFromReitoria(hashServidoresTaes.get("jaCadastrados"), "tae");
		} catch (NamingException e) {
			success = false;
			e.printStackTrace();
		}
		
		try {
			atualizaServidorsFromReitoria(hashServidoresTerceirizados.get("jaCadastrados"), "terceirizado");
		} catch (NamingException e) {
			success = false;
			e.printStackTrace();
		}
		
		return success;
	}

	private HashMap<String, List<Servidor>> transformaListaServidoresEmHash(List<Servidor> listaDeServidors) {
		HashMap<String, List<Servidor>> hashServidores = new HashMap<String, List<Servidor>>();
		List<Servidor> listaServidoresDocentes = new ArrayList<Servidor>();
		List<Servidor> listaServidoresTaes = new ArrayList<Servidor>();
		List<Servidor> listaServidoresTerceirizados = new ArrayList<Servidor>();
		
		for (Servidor servidor : listaDeServidors) {
			//String tipoDeServidor = "";
			//List<ServidorCargo> cargosServidor = servidor.getListaCargos();
			
			if (servidor.getTipoServidor().equalsIgnoreCase("docente"))
				listaServidoresDocentes.add(servidor);
			else if (servidor.getTipoServidor().equalsIgnoreCase("tae"))
				listaServidoresTaes.add(servidor);
			else if(servidor.getTipoServidor().trim().equalsIgnoreCase("terceirizado"))
				listaServidoresTerceirizados.add(servidor);
		}
		hashServidores.put("docente", listaServidoresDocentes);
		hashServidores.put("tae", listaServidoresTaes);
		hashServidores.put("terceirizado", listaServidoresTerceirizados);
		
		return hashServidores;
	}

	private HashMap<String, List<Servidor>> retornaHashDeServidorsFiltrados(List<Servidor> listaTodosServidors,
			String tipoDeServidor) {
		List<Servidor> listaServidoresJaCadastrados = new ArrayList<Servidor>();
		List<Servidor> listaServidoresNaoCadastrados = new ArrayList<Servidor>();

		MainAdCrud mainAd = new MainAdCrud(tipoDeServidor);
		mainAd.newConnection();
		HashMap<String, User> hashMapServidorsFromAd = mainAd.returnUserHashMap();
		for (Servidor servidor : listaTodosServidors) {
			
			if (hashMapServidorsFromAd.get( servidor.getCn().toLowerCase() ) != null) {
				listaServidoresJaCadastrados.add(servidor);

			} else if( hashMapServidorsFromAd.get(servidor.getCn().toLowerCase()) == null) {
				listaServidoresNaoCadastrados.add(servidor);

			}

		}
		HashMap<String, List<Servidor>> hashServidors = new HashMap<String, List<Servidor>>();
		hashServidors.put("jaCadastrados", listaServidoresJaCadastrados);
		hashServidors.put("naoCadastrados", listaServidoresNaoCadastrados);
		return hashServidors;
	}

	private void persisteServidoresFromReitoria(List<Servidor> listaDeServidors, String tipoDeServidor)
			throws javax.naming.directory.InvalidAttributeValueException, javax.naming.NameAlreadyBoundException {

		for (Servidor servidor : listaDeServidors) {
			MainAdCrud mainAd = new MainAdCrud(tipoDeServidor);
			mainAd.newConnection();
			//String[] nomeQuebradoNosEspacos = servidor.getNome_completo().split(" ");
			System.out.println("servidor para ser persistido: "+servidor.getCn());
			System.out.println("Senha do servidor: " + servidor.getSenha());

			User user = convertServidorToUser(servidor);
			try {
				mainAd.addUser(user);
			} finally {
				mainAd.closeConnection();
			}

		}

	}

	private User convertServidorToUser(Servidor servidor) {
		String[] vetorNomes = servidor.getNome_completo().split(" ");
		User user = new User();

		user.setCn( servidor.getCn().toLowerCase() );
		user.setGivenName(vetorNomes[0].toLowerCase());
		if (servidor.getEmail() != null)
			user.setMail(servidor.getEmail().toLowerCase());
		user.setName(servidor.getCn());
		System.out.println("Servidor: "+servidor.getCn() + " password: "+ servidor.getSenha());
		user.setPassword(CriptografiaUtil.desencriptar(servidor.getSenha()));
		user.setSamaccountname( (vetorNomes[0] + "." + vetorNomes[vetorNomes.length - 1]).toLowerCase() );
		user.setSn(servidor.getCn().toLowerCase());

		return user;
	}

	private void atualizaServidorsFromReitoria(List<Servidor> listaDeServidors, String tipoDeServidor)
			throws NamingException {
		MainAdCrud mainAdConnection = new MainAdCrud(tipoDeServidor);
		mainAdConnection.newConnection();
		HashMap<String, User> hashMapServidorsFromAd = mainAdConnection.returnUserHashMap();
		mainAdConnection.closeConnection();

		for (int i = 0; i < listaDeServidors.size(); i++) {
			Servidor servidorFromBase = listaDeServidors.get(i);
			MainAdCrud mainAd = new MainAdCrud(tipoDeServidor);
			mainAd.newConnection();
			String[] vetorNomes = servidorFromBase.getNome_completo().split(" ");

			User usuarioAd = hashMapServidorsFromAd.get(servidorFromBase.getCn().toLowerCase()  );

			if (!usuarioAd.getGivenName().equalsIgnoreCase(vetorNomes[0]))
				atualizarAtributo(usuarioAd.getCn(), "givenName", vetorNomes[0], mainAd);
/*
			if ((!usuarioAd.getSamaccountname().equalsIgnoreCase(servidorFromBase.getCn())))
				atualizarAtributo(usuarioAd.getCn(), "samAccountName", servidorFromBase.getLogin(), mainAd);
*/
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
				System.out.println("USUÁRIO ESTÁ INATIVO: "+ servidorFromBase.getCn());
				/*
				Attribute userAccountControlAttr = searchResult.getAttributes().get("userAccountControl");
				userAccountControlValue |= 2; // seta o segundo bit (0x00000002) para desativar a conta
				atualizarAtributo(usuarioAd.getCn(), "userAccountControl", Integer.toString(USER_ACCOUNT_DISABLED),mainAd);
*/
				mainAd.disableAccount(servidorFromBase.getCn());
				mainAd.closeConnection();
			}
			if (isAtivo) {
				System.out.println("USUÁRIO ESTÁ ATIVO: "+ servidorFromBase.getCn());
				
				mainAd.enableAccount(servidorFromBase.getCn());
				mainAd.closeConnection();
			}
		}
	}

	private void atualizarAtributo(String userCN, String attribute, Object value, MainAdCrud mainAd)
			throws NamingException {
		mainAd.modifyAdAttribute(userCN, attribute.toLowerCase(), value);

	}
	
	
}
