package br.edu.ifrs.ibiruba.ldapmanager.useful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.AlunoCursoRepository;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;

@Service
public class UserUseful {
	Logger logger = LoggerFactory.getLogger(UserUseful.class);

	public String returnSpecifiedTypeOfUser(AlterPasswordModel alterPassword, ServidorRepository servidorRepository,
			AlunoCursoRepository alunoCursoRepository) {

		String specifiedTypeOfUser = "";
		// HashMap<String, User> usuarios;

		if (alterPassword.getTypeOfUser().trim().equalsIgnoreCase("servidor")) {
			try {
				specifiedTypeOfUser = servidorRepository.findBycn(alterPassword.getUser()).get().getTipoServidor();
			} catch (Exception e) {
				logger.error("erro para retornar o tipo especificado de usuário: " + e.getStackTrace());
			}

		} else if (alterPassword.getTypeOfUser().equalsIgnoreCase("aluno")) {

			AlunoCurso alunoCurso = null;
			try {
				alunoCurso = alunoCursoRepository.getById(alterPassword.getUser());
			} catch (Exception e) {
				logger.error("erro para retornar o tipo especificado de usuário: " + e.getStackTrace());
			}
			if (alunoCurso.getNome_curso().equalsIgnoreCase("MATEMÁTICA - LICENCIATURA")
					|| alunoCurso.getNome_curso().equalsIgnoreCase("BACHAREL EM CIÊNCIA DA COMPUTAÇÃO")
					|| alunoCurso.getNome_curso().equalsIgnoreCase("MECÂNICA")
					|| alunoCurso.getNome_curso().equalsIgnoreCase("ELETROTÉCNICA")) {
				// falta engenharia mecânica e agronomia migrar para o sigaa
				specifiedTypeOfUser = "superior";

			} else if (alunoCurso.getNome_curso().equalsIgnoreCase("AGROPECUÁRIA - INTEGRADO AO ENSINO MÉDIO")
					|| alunoCurso.getNome_curso().equalsIgnoreCase("INFORMÁTICA - INTEGRADO AO ENSINO MÉDIO")
					|| alunoCurso.getNome_curso().equalsIgnoreCase("MECÂNICA - INTEGRADO AO ENSINO MÉDIO")) {

				specifiedTypeOfUser = "integrado";
			}
		}
		System.out.println("tipo de usuário: " + specifiedTypeOfUser);

		return specifiedTypeOfUser;
	}

}
