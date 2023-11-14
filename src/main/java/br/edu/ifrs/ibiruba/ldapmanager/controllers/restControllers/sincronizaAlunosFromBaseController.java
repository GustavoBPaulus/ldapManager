package br.edu.ifrs.ibiruba.ldapmanager.controllers.restControllers;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifrs.ibiruba.ldapmanager.services.AddAlunosFromBase;


@RestController
@RequestMapping("/atualiza-ldapalunos")
public class sincronizaAlunosFromBaseController {
	@Autowired
	AddAlunosFromBase alunosFromBase;
	
	@GetMapping
	public ResponseEntity<?> updateStudents() throws NamingException {
		boolean alunosSincronizados = alunosFromBase.addLdapAlunosFromBase();
		//System.out.println("sincronizado: "+ (alunosSincronizados) );
		return ResponseEntity.ok(alunosSincronizados);
	}
	
}
