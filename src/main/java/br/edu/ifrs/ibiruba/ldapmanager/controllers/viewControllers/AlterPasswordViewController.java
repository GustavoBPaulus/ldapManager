package br.edu.ifrs.ibiruba.ldapmanager.controllers.viewControllers;

import javax.naming.NameAlreadyBoundException;
import javax.naming.directory.InvalidAttributeValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.services.AddServidoresFromBase;
import br.edu.ifrs.ibiruba.ldapmanager.services.ChangePasswordService;
import br.edu.ifrs.ibiruba.ldapmanager.services.ResetPasswordService;

@Controller
@RequestMapping("/altermypassword")
public class AlterPasswordViewController {

	@Autowired
	ChangePasswordService changePasswordService;

	@Autowired
	ResetPasswordService resetPasswordService;
	
	@Autowired
	AddServidoresFromBase servidoresFromBaseService;
	
	Logger logger = LoggerFactory.getLogger(AlterPasswordViewController.class);

	@RequestMapping
	public String pesquisar(ModelMap m) {		
		m.addAttribute("alterPasswordModel", new AlterPasswordModel());
		return "AlterMyPassword";
	}

	@PostMapping("/forgot-password")
	public String forgetPassword(AlterPasswordModel alterPasswordModel, RedirectAttributes redirectAttrs) {
		boolean passwordChangedAndSended = resetPasswordService.forgetPassword(alterPasswordModel.getUser(),
				alterPasswordModel.getTypeOfUser());
		
		redirectAttrs.addFlashAttribute("passwordChangedAndSended", passwordChangedAndSended);
		
		System.out.println("recebeu a requisição");
		
		try {
			servidoresFromBaseService.addServidorsFromBase();
		} catch (InvalidAttributeValueException | NameAlreadyBoundException e) {
			logger.error("erro ao resetar a senha, método forgetPassword: "+ e.getStackTrace());
			e.printStackTrace();
		}
		
		logger.info("método forget password executado com sucesso");
		return "redirect:/altermypassword";
	}

	@PostMapping("/changepassword")
	public String changePassword(AlterPasswordModel alterPasswordModel, RedirectAttributes redirectAttrs) {
		boolean passwordChanged = false;
		
		System.out.println("TIPO DE USUÁRIO: "+alterPasswordModel.getTypeOfUser());
		System.out.println("objeto alterPasswordModel: "+alterPasswordModel.getTypeOfUser());
		passwordChanged = changePasswordService.alterPassword(alterPasswordModel);
		
		redirectAttrs.addFlashAttribute("passwordChanged", passwordChanged);
		try {
			servidoresFromBaseService.addServidorsFromBase();
		} catch (InvalidAttributeValueException | NameAlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("método change password executado com sucesso");
		return "redirect:/altermypassword";
	}
}
