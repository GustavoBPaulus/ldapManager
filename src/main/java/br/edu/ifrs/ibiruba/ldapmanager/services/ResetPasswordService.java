package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.util.Random;

import br.edu.ifrs.ibiruba.ldapmanager.useful.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;
import br.edu.ifrs.ibiruba.ldapmanager.entities.MessageModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoCursoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.MainAdCrud;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;
import br.edu.ifrs.ibiruba.ldapmanager.useful.SendMail;
import br.edu.ifrs.ibiruba.ldapmanager.useful.UserUseful;

@Service
public class ResetPasswordService {

	@Autowired
	AlunoRepository alunoRepository;

	@Autowired
	AlunoCursoRepository alunoCursoRepository;

	@Autowired
	ServidorRepository servidorRepository;

	@Autowired
	ServidorAuthenticatorService servidorAuthenticatorService;
	Logger logger = LoggerFactory.getLogger(ResetPasswordService.class);

	public boolean forgetPassword(String user, String tipoUsuario) {
		// boolean userIsAstudent = false;
		String urlAlterarSenha = "http://admin.ibiruba.ifrs.edu.br/usuarios/altermypassword";

		AlterPasswordModel alterPassword = new AlterPasswordModel();
		alterPassword.setUser(user);
		alterPassword.setTypeOfUser(tipoUsuario);
		//System.out.println("user: " + user + " tipo usuário: " + tipoUsuario);

		String tipoAlunoOuServidor = new UserUseful().returnSpecifiedTypeOfUser(alterPassword, servidorRepository,
				alunoCursoRepository);

		//System.out.println("tipo de aluno ou servidor: " + tipoAlunoOuServidor);
		// instância um objeto da classe Random usando o construtor padrão
		Random gerador = new Random();

		// Gera uma senha aleatória
		String senhaTemporaria = PasswordUtil.retornaSenhaTemporaria();

		//boolean changedAdAdm = false;
		boolean changedBase = false;

		// enviar email com a senha nova e link para alterar a senha com base na antiga,
		// aí ele primeiro autentica se autenticar certo permite alterar a senha
		MessageModel message = new MessageModel();

		message.setDestination(getMailFromUser(user, tipoUsuario));
		message.setRemetent("timanager@ibiruba.ifrs.edu.br");
		message.setSubject("Nova senha temporária");
		message.setText("A nova senha para o usuário: " + user + " é: " + senhaTemporaria
				+ "\n Para alterar essa senha " + "para uma de sua preferência acesse: " + urlAlterarSenha
				+ " a senha temporaria serve apenas para alterar a senha, não funcionará para acessar a internet ou outro sistema,"
				+ "portanto sua senha precisa ser alterada");

		boolean sended = new SendMail().sendMailLogic(message);
		if (sended) {

			//changedAdAdm = new MainAdCrud(tipoAlunoOuServidor).changePassword(user, senhaTemporaria);
			////System.out.println("senha alterada: " + changedAdAdm);
			////System.out.println("tipo de usuário: " + tipoUsuario);
			
			/*
			 * Para não ter problemas de um sacanear o outro, o esqueci minha senha não altera a senha ele altera
			 * o campo senha temporária e ativa o campo isSenhaTemporariaAtiva, portanto a senha gerada pelo esqueci minha senha
			 * apenas serve para alterar senha, e não pode ser utilizada para acessar nenhum sistema
			 */

			if (tipoUsuario.equalsIgnoreCase("servidor")) {
				//System.out.println("entrou no if do servidor");
				Servidor servidor = servidorRepository.findBycn(user).get();
				servidor.setSenhaTemporaria(CriptografiaUtil.encriptar(senhaTemporaria));
				servidor.setTemporariaAtiva(true);
				changedBase = servidorRepository.save(servidor).getLogin() != null;
				//atualiza o status na tabela de serviço
				servidorAuthenticatorService.save(servidor);

			} else if (tipoUsuario.equalsIgnoreCase("aluno")) {
				//System.out.println("entrou no if do aluno");
				AlunoCurso alunoCurso = alunoCursoRepository.getById(user);
				Aluno aluno = alunoRepository.findById(alunoCurso.getAluno().getLogin()).get();
				aluno.setSenhaTemporaria(CriptografiaUtil.encriptar(senhaTemporaria));
				aluno.setTemporariaAtiva(true);
				changedBase = alunoRepository.save(aluno).getLogin() != null;
			}

		}
		return  sended && changedBase;
	}

	private String getMailFromUser(String user, String tipoUsuario) {
		//System.out.println("tipo Usuario: " + tipoUsuario);

		if (tipoUsuario.equalsIgnoreCase("servidor") || tipoUsuario.equalsIgnoreCase("tae")
				|| tipoUsuario.equalsIgnoreCase("docente"))
			return servidorRepository.findBycn(user).get().getEmail();
		else {
			AlunoCurso alunoCurso = alunoCursoRepository.getById(user);
			Aluno aluno = alunoRepository.findById(alunoCurso.getAluno().getLogin()).get();
			return aluno.getEmail();
		}
	}

	public static void main(String[] args) {
		new ResetPasswordService().forgetPassword("gustavo.paulus", "tae");
	}
}
