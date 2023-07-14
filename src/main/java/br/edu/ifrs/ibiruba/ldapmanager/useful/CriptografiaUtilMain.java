package br.edu.ifrs.ibiruba.ldapmanager.useful;

import org.springframework.security.crypto.password.PasswordEncoder;


public class CriptografiaUtilMain implements PasswordEncoder {

	public static String encriptar(String x) {
		int chave = 5;
		String msgCript = "";
		for (int i = 0; i < x.length(); i++) {
			msgCript += (char) (x.charAt(i) + chave);
		}
		return msgCript;
	}

	public static String desencriptar(String msgCript) {
		int chave = 5;
		String msg = "";
		for (int i = 0; i < msgCript.length(); i++) {
			msg += (char) (msgCript.charAt(i) - chave);
		}
		return msg;
	}

	
	public String encode(CharSequence rawPassword) {
		int chave = 5;
		String msgCript = "";
		for (int i = 0; i < rawPassword.length(); i++) {
			msgCript += (char) (rawPassword.charAt(i) + chave);
		}
		return msgCript;
	}

	
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String encondedRawPassword = encode(rawPassword);
		if (encondedRawPassword.equalsIgnoreCase(encodedPassword))
			return true;
		else
			return false;
	}
	
	
	public static void main(String []args) {
		//CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		System.out.println(desencriptar("57:6=69=5<=Engnwzgf3nkwx"));
	}
}
