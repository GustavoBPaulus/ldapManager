package br.edu.ifrs.ibiruba.ldapmanager.controllers.restControllers;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.edu.ifrs.ibiruba.ldapmanager.services.AddServidoresFromBase;


@RestController
@RequestMapping("/atualiza-ldapservidores")
public class SincronizarServidoresFromBaseController {
	@Autowired
	AddServidoresFromBase servidoresFromBase;
	
	@GetMapping
	public ResponseEntity<?> updateStudents() throws NamingException {
		boolean alunosSincronizados = servidoresFromBase.addServidorsFromBase();
		System.out.println("sincronizado: "+ (alunosSincronizados) );
		return ResponseEntity.ok(alunosSincronizados);
	}
	
	
}
