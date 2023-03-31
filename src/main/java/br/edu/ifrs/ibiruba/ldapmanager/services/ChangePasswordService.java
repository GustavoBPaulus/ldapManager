package br.edu.ifrs.ibiruba.ldapmanager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoCursoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.MainAdCrud;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;
import br.edu.ifrs.ibiruba.ldapmanager.useful.UserUseful;

@Service
public class ChangePasswordService {
	@Autowired
	ServidorRepository servidorRepository;

	@Autowired
	AlunoRepository alunoRepository;

	@Autowired
	AlunoCursoRepository alunoCursoRepository;

	public boolean alterPassword(AlterPasswordModel alterPasswordModel) {
		String tipoDeAlunoOuTipoDeServidor = new UserUseful().returnSpecifiedTypeOfUser(alterPasswordModel,
				servidorRepository, alunoCursoRepository);
		boolean userWasAutenticated = false;
		if (alterPasswordModel.getTypeOfUser().equalsIgnoreCase("servidor")) {
			Servidor servidor = servidorRepository.findBycn(alterPasswordModel.getUser().trim()).get();
			System.out.println("senha atual encriptada: " + servidor.getSenha());
			String senhaAtualDesencriptada = CriptografiaUtil.desencriptar(servidor.getSenha());
			String senhaTemporariaDesencriptada = null;
			System.out.println("senha desincriptada: " + senhaAtualDesencriptada);

			if (servidor.isTemporariaAtiva() && servidor.getSenhaTemporaria() != null) {
				senhaTemporariaDesencriptada = CriptografiaUtil.desencriptar(servidor.getSenhaTemporaria());

				if (alterPasswordModel.getActualPassword().equals(senhaTemporariaDesencriptada))
					userWasAutenticated = true;
			}

			if (alterPasswordModel.getActualPassword().equals(senhaAtualDesencriptada))
				userWasAutenticated = true;

		} else if (alterPasswordModel.getTypeOfUser().equalsIgnoreCase("aluno")) {
			AlunoCurso alunoCurso = alunoCursoRepository.getById(alterPasswordModel.getUser());
			Aluno aluno = alunoRepository.findById(alunoCurso.getAluno().getLogin()).get();
			String senhaAtualDesencriptada = CriptografiaUtil.desencriptar(aluno.getSenha());
			String senhaTemporariaDesencriptada = null;

			if (aluno.isTemporariaAtiva() && aluno.getSenhaTemporaria() != null) {
				senhaTemporariaDesencriptada = CriptografiaUtil.desencriptar(aluno.getSenhaTemporaria());
				if (alterPasswordModel.getActualPassword().equals(senhaTemporariaDesencriptada ))
					userWasAutenticated = true;
			}

			if (alterPasswordModel.getActualPassword().equals(senhaAtualDesencriptada))
				userWasAutenticated = true;
		}

		boolean passwordChangedAd = false;
		boolean passwordChangedBase = false;

		if (userWasAutenticated) {
			passwordChangedAd = new MainAdCrud(tipoDeAlunoOuTipoDeServidor)
					.changePassword(alterPasswordModel.getUser().trim(), alterPasswordModel.getNewPassword());

			if (alterPasswordModel.getTypeOfUser().equalsIgnoreCase("servidor")) {
				Servidor servidor = servidorRepository.findBycn(alterPasswordModel.getUser().trim()).get();
				servidor.setSenha(CriptografiaUtil.encriptar(alterPasswordModel.getNewPassword()));
				servidorRepository.save(servidor);
				passwordChangedBase = true;
				//se foi alterada corretamente passa senha temporaria ativa para false
				if(passwordChangedBase)
					servidor.setTemporariaAtiva(false);
				
			} else if (alterPasswordModel.getTypeOfUser().equalsIgnoreCase("aluno")) {
				AlunoCurso alunoCurso = alunoCursoRepository.getById(alterPasswordModel.getUser());
				Aluno aluno = alunoRepository.findById(alunoCurso.getAluno().getLogin()).get();
				aluno.setSenha(CriptografiaUtil.encriptar(alterPasswordModel.getNewPassword()));
				alunoRepository.save(aluno);
				passwordChangedBase = true;
				//se foi alterada corretamente passa senha temporaria ativa para false
				if(passwordChangedBase)
					aluno.setTemporariaAtiva(false);
			}

		}
		
		
		
		return passwordChangedAd && passwordChangedBase;
	}

	public static void main(String[] args) {
		AlterPasswordModel alterPasswordModel = new AlterPasswordModel();
		alterPasswordModel.setUser("02378150016");
		alterPasswordModel.setActualPassword("ifrs-123456");
		alterPasswordModel.setNewPassword("StrongPassword");

	}
}
