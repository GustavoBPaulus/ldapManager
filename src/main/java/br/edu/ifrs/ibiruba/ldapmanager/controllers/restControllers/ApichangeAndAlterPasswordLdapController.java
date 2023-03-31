package br.edu.ifrs.ibiruba.ldapmanager.controllers.restControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.services.ChangePasswordService;
import br.edu.ifrs.ibiruba.ldapmanager.services.ResetPasswordService;


@Controller
@RequestMapping("/userpasswordmanager")
public class ApichangeAndAlterPasswordLdapController {
	
	@Autowired
	ChangePasswordService changePasswordService;
	
	@Autowired
	ResetPasswordService resetPasswordService;
	
	
	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgetPassword(@RequestBody AlterPasswordModel alterPasswordModel) {
		boolean passwordChangedAndSended = resetPasswordService.forgetPassword(alterPasswordModel.getUser()
				, alterPasswordModel.getTypeOfUser());
		return ResponseEntity.ok(passwordChangedAndSended);
	}


	@PostMapping("/changepassword")
	public  ResponseEntity<?> changePassword(@RequestBody AlterPasswordModel alterPasswordModel) {
		boolean passwordChanged = false;
		passwordChanged = changePasswordService.alterPassword(alterPasswordModel);
		
		return ResponseEntity.ok(passwordChanged);
	}

}
