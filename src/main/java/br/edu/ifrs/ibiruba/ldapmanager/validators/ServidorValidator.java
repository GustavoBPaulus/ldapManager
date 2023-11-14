package br.edu.ifrs.ibiruba.ldapmanager.validators;

import java.util.Optional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.ServidorRepository;

public class ServidorValidator implements Validator {

	private ServidorRepository servidorRepository;

	public ServidorValidator(ServidorRepository servidorRepository) {
		this.servidorRepository = servidorRepository;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Servidor.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Servidor servidor = (Servidor) target;

		Optional<Servidor> servidorFound = servidorRepository.findByEmail(servidor.getEmail());
		Servidor servidorFoundObject = new Servidor();

		// login
		servidorFound = servidorRepository.findByLogin(servidor.getLogin());
		if (servidorFound.isPresent()) {
			servidorFoundObject = servidorFound.get();
			boolean foundIsEqualActual = (servidor.getCn().equalsIgnoreCase(servidorFoundObject.getCn())
					&& (servidor.getLogin().equalsIgnoreCase(servidorFoundObject.getLogin())) ? true : false);
			if (!foundIsEqualActual) {
				//System.out.println("servidorFound é diferente de Servidor: " + !servidorFound.get().equals(servidor));
				//System.out.println("to String servidor found: " + servidorFound.toString());
				//System.out.println("to String servidor: " + servidorFound.toString());
				errors.rejectValue("login", "validacao.servidor.login.existente");
			}
			if (servidor.getLogin() == null || servidor.getLogin().equals("")) {
				errors.rejectValue("login", "validacao.servidor.login.vazio");
			}
			if (!servidor.getLogin().equals(servidorFoundObject.getLogin())) {
				errors.rejectValue("login", "validacao.servidor.login.naopodeseralterado");
			}
		}
		// cn
		servidorFound = servidorRepository.findBycn(servidor.getCn());
		if (servidorFound.isPresent()) {
			servidorFoundObject = servidorFound.get();
			boolean foundIsEqualActual = (servidor.getCn().equalsIgnoreCase(servidorFoundObject.getCn())
					&& (servidor.getLogin().equalsIgnoreCase(servidorFoundObject.getLogin())) ? true : false);
			//System.out.println("servidor: " + servidor);
			//System.out.println("servidor found: " + servidorFound.toString());
			if (!foundIsEqualActual) {
				errors.rejectValue("cn", "validacao.servidor.cn.existente");
			}
			if (servidor.getCn() == null || servidor.getCn().equals("")) {
				errors.rejectValue("cn", "validacao.servidor.cn.vazio");
			}
			if (!servidor.getCn().equals(servidorFoundObject.getCn())) {
				errors.rejectValue("cn", "validacao.servidor.cn.naopodeseralterado");
			}
		}

		// email
		servidorFound = servidorRepository.findByEmail(servidor.getEmail());
		if (servidorFound.isPresent()) {
			servidorFoundObject = servidorFound.get();
			boolean foundIsEqualActual = (servidor.getCn().equalsIgnoreCase(servidorFoundObject.getCn())
					&& (servidor.getLogin().equalsIgnoreCase(servidorFoundObject.getLogin())) ? true : false);
			if (!foundIsEqualActual) {
				errors.rejectValue("email", "validacao.servidor.email.existente");
			}
		}
		
		if (servidor.getEmail() == null || servidor.getEmail().equals("")) {
			errors.rejectValue("email", "validacao.servidor.email.vazio");
		}

	}

}
