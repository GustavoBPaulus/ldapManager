package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.naming.NameAlreadyBoundException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.MessageModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.entities.ServidorCargo;
import br.edu.ifrs.ibiruba.ldapmanager.exceptions.ServidorNaoEncontradoException;
import br.edu.ifrs.ibiruba.ldapmanager.exceptions.ServidorPossuiCargoException;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorCargoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;
import br.edu.ifrs.ibiruba.ldapmanager.useful.SendMail;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
public class ServidorCrudService {

	@Autowired
	ServidorRepository servidorRepository;

	@Autowired
	ServidorCargoRepository servidorCargoRepository;

	@Autowired
	AddServidoresFromBase servidoresFromBaseService;

	@Autowired
	ChangePasswordService changePasswordService;

	public List<Servidor> findAll() {

		return servidorRepository.findAll();
	}

	

	public List<Servidor> findByCnStatusAndTipoServidor(String cn, String status, String tipoDeServidor) {
		System.out.println("cn: " + cn + " status: " + status);
	status =	status.toUpperCase();
		//todos 
		if (cn.equals("") && status.equalsIgnoreCase("todos") && tipoDeServidor.equalsIgnoreCase("todos") ) {
			System.out.println("caiu no if de todos ,findAll: cn vazio e status = todos ");

			return findAll();

		} 
		//apenas pelo cn
		else if (!cn.equals("") && status.equalsIgnoreCase("todos") && tipoDeServidor.equalsIgnoreCase("todos")) {
			System.out.println("caiu no if apenas pelo cn ,cn: " + cn + " status: " + status);

			return servidorRepository.findBycnContaining(cn.trim().toLowerCase());

		} //apenas o status
		else if(cn.equals("") && !status.equalsIgnoreCase("todos") && tipoDeServidor.equalsIgnoreCase("todos")){
			System.out.println("caiu no if apenas pelo status ,cn: " + cn + " status: " + status);
			return servidorRepository.findBytatusIsEqual(status);

		}
		//apenas pelo tipo de servidor
		else if(cn.equals("") && status.equalsIgnoreCase("todos") && !tipoDeServidor.equalsIgnoreCase("todos")){
			System.out.println("caiu no if apenas pelo tipo ,cn: " + cn + " status: " + status);
			return servidorRepository.findByTipoServidor(tipoDeServidor.toLowerCase().trim());

		}
		
		//apenas cn e status
		else if(!cn.equals("") && !status.equalsIgnoreCase("todos") && tipoDeServidor.equalsIgnoreCase("todos")){
			System.out.println("caiu no if apenas pelo cn e status ,cn: " + cn + " status: " + status);
			return servidorRepository.findBycnContainingAndStatusIsEqual(cn.trim().toLowerCase(),
					status.toUpperCase().trim());

		}
		//apenas cn e tipo
		else if(!cn.equals("") && status.equalsIgnoreCase("todos") && !tipoDeServidor.equalsIgnoreCase("todos")) {
			
			System.out.println("if do cn e tipo, cn = " +cn + "status = "+status + "tipoDeServidor = "+tipoDeServidor);
			return servidorRepository.findBycnContainingAndTipo(cn, tipoDeServidor);
	
		}
		//apenas tipo e status
		else if(cn.equals("") && !status.equalsIgnoreCase("todos") && !tipoDeServidor.equalsIgnoreCase("todos")) {
			System.out.println("if tipo e status, cn = " +cn + "status = "+status + "tipoDeServidor = "+tipoDeServidor);
			return servidorRepository.findByStatusAndTipo(status.toUpperCase(), tipoDeServidor);
		}
		//tipo, status e cn
		else {
			System.out.println("if cn, tipo e status preenchidos, cn = " +cn + "status = "+status + "tipoDeServidor = "+tipoDeServidor);
			return servidorRepository.findBycnContainingAndStatusAndTipoAreEqual(cn, status, tipoDeServidor);
		}
	}

	public Servidor findByCpf(String cpf) {
		return servidorRepository.findByLogin(cpf).orElseThrow(() -> new ServidorNaoEncontradoException(cpf));
	}
	/*
	 * public Servidor update(Servidor servidor) {
	 * 
	 * 
	 * // servidor.setSenha(CriptografiaUtil.encriptar(servidor.getSenha()));
	 * 
	 * return servidorRepository.save(servidor); }
	 */

	public Servidor save(@Valid Servidor servidor) {
		// trata cn
		trataServidor(servidor);
		servidorRepository.save(servidor);
		Optional<ServidorCargo> servidorCargoOptional = servidorCargoRepository.findById(servidor.getLogin());

		List<ServidorCargo> listaDeCargos = new ArrayList<ServidorCargo>();

		// cadastra Servidor Cargo
		if (servidorCargoOptional.isEmpty()) {
			ServidorCargo servidorCargo = new ServidorCargo();

			// quando o cara chega, ainda não tem siape, então eu seto o cpf como matricula
			servidorCargo.setMatricula(servidor.getLogin());
			// o cargo de quem não sincroniza com a reitoria seto o tipo do servidor Docente
			// ou TAE ou tercerizado
			servidorCargo.setCargo(servidor.getTipoServidor());
			// quando cadastra o status vai ser ativo
			servidorCargo.setStatus("ATIVO");
			// login
			servidorCargo.setServidor(servidor);

			listaDeCargos.add(servidorCargo);
			servidor.setListaCargos(listaDeCargos);

			servidorCargoRepository.save(servidorCargo);
			servidorRepository.save(servidor);
		}

			adicionaStatusDosCargosNaTabelaServidor();

		try {
			servidoresFromBaseService.addServidorsFromBase();
		} catch (InvalidAttributeValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NameAlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// emailDeNovoUsuário(servidor);
		return servidor;
	}

	private void trataServidor(Servidor servidor) {
		servidor.setCn(servidor.getCn().toLowerCase());
		servidor.setCn(servidor.getCn().trim());
		servidor.setCn(semAcento(servidor.getCn()));
		
		servidor.setEmail(semAcento(servidor.getEmail().toLowerCase()));
		
		servidor.setNome_completo(semAcento(servidor.getNome_completo()));
		
		servidor.setLogin(apenasNumeros(servidor.getLogin()));
	}

	public void emailDeNovoUsuário(Servidor servidor) {
		MessageModel message = new MessageModel();

		message.setDestination(servidor.getEmail() + ",ti@ibiruba.ifrs.edu.br");
		message.setRemetent("timanager@ibiruba.ifrs.edu.br");
		message.setSubject("Novo usuário ifrs - campus ibirubá");
		message.setText("Seja bem vindo ao ifrs - campus ibirubá, seu usuário é: " + servidor.getCn()
				+ " sua senha temporária é: " + CriptografiaUtil.desencriptar(servidor.getSenha())
				+ " esse usuário e senha devem ser utilizado para acessar a rede wifi servidores");

		boolean sended = new SendMail().sendMailLogic(message);
	}

	public List<String> retornaPerfis() {
		return servidorRepository.selectDistinctPerfis();

	}

	public void delete(Servidor servidor) {
		//if (servidorCargoRepository.findById(servidor.getCn()).isEmpty()) {
		//se for excluir o servidor vai excluir os cargos também
			servidor.getListaCargos().forEach(c ->{
				servidorCargoRepository.delete(c);
			});
			servidorRepository.delete(servidor);
				//varre todos os servidores que tem no ldap e não tem na base
				servidoresFromBaseService.exluirUsuariosQueExistemNaBaseEnaoExistemNoLdap();

	/*	} else
			throw new ServidorPossuiCargoException(servidor.getCn());
	*/

	}


	public void inativar(Servidor servidor) {
		List<ServidorCargo> listaDeservidorCargo = servidorCargoRepository.findByServidor(servidor);

		if (listaDeservidorCargo != null || !listaDeservidorCargo.isEmpty()) {

			listaDeservidorCargo.forEach(sc -> {
				sc.setStatus("INATIVO");
				servidorCargoRepository.save(sc);
			});

			adicionaStatusDosCargosNaTabelaServidor();
			try {
				servidoresFromBaseService.addServidorsFromBase();
			} catch (InvalidAttributeValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NameAlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			throw new ServidorPossuiCargoException(servidor.getCn());
	}

	public void ativar(Servidor servidor) {
		List<ServidorCargo> listaDeservidorCargo = servidorCargoRepository.findByServidor(servidor);

		if (listaDeservidorCargo != null || !listaDeservidorCargo.isEmpty()) {

			listaDeservidorCargo.forEach(sc -> {
				sc.setStatus("ATIVO");
				servidorCargoRepository.save(sc);
			});

			adicionaStatusDosCargosNaTabelaServidor();
			try {
				servidoresFromBaseService.addServidorsFromBase();
			} catch (InvalidAttributeValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NameAlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			throw new ServidorPossuiCargoException(servidor.getCn());
	}

	public boolean resetarSenhaServidor(Servidor servidor) {

		String senhaReset = servidor.getLogin() + "@ibiruba.ifrs".trim();
		AlterPasswordModel alterPasswordModel = new AlterPasswordModel();
		alterPasswordModel.setActualPassword(CriptografiaUtil.desencriptar(servidor.getSenha()));
		alterPasswordModel.setNewPassword(senhaReset);
		alterPasswordModel.setTypeOfUser("servidor");
		alterPasswordModel.setUser(servidor.getCn());

		return changePasswordService.alterPassword(alterPasswordModel);

	}

	public void setaStatusServidorComBaseEmCargos(Servidor servidor) {
		List<ServidorCargo> cargosServidorAtual = servidorCargoRepository.findByServidor(servidor);
		cargosServidorAtual.forEach(c -> {
			if (c.getStatus().equalsIgnoreCase("ATIVO"))
				servidor.setStatus("ATIVO");
			else
				servidor.setStatus("INATIVO");
		});

	}

	public void adicionaStatusDosCargosNaTabelaServidor() {

		List<Servidor> listaDeServidores = findAll();

		listaDeServidores.forEach(s -> {
			setaStatusServidorComBaseEmCargos(s);
			servidorRepository.save(s);
		});
	}

	public static String semAcento(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	public static String apenasNumeros(String login){
		char numero[] = login.toCharArray();
		StringBuilder loginLimpo = new StringBuilder();
		for(char digito : numero){
			if(Character.isDigit(digito))
				loginLimpo.append(digito);
		}

	return loginLimpo.toString();
	}

}
