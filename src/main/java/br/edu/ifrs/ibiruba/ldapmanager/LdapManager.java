package br.edu.ifrs.ibiruba.ldapmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class LdapManager extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(LdapManager.class, args);
	}

}
