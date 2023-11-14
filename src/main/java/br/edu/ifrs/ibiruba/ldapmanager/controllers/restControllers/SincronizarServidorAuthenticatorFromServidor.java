package br.edu.ifrs.ibiruba.ldapmanager.controllers.restControllers;

import br.edu.ifrs.ibiruba.ldapmanager.services.PersisteServidorAuthenticatorComBaseNoServidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.NamingException;


@RestController
@RequestMapping("/atualiza-servidorAuthenticator")
public class SincronizarServidorAuthenticatorFromServidor {
	@Autowired
	PersisteServidorAuthenticatorComBaseNoServidorService persisteServidorAuthenticatorComBaseNoServidorService;
	
	@GetMapping
	public ResponseEntity<?> updateStudents() throws NamingException {
		boolean sincronizado = persisteServidorAuthenticatorComBaseNoServidorService.percorreListaDeServidoresEpersisteNaServidoresAuthenticator();

		return ResponseEntity.ok(sincronizado);
	}
	
	
}
