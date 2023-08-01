package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

import br.edu.ifrs.ibiruba.ldapmanager.entities.User;

public class MainAdCrud {

	private String caminhoCnOuUnidadeOrganizacional = "";
	DirContext connection = null;

	public MainAdCrud(String tipoUsuario) {
		System.out.println("tipo de servidor: " + tipoUsuario);
		if (tipoUsuario.equalsIgnoreCase("integrado"))
			caminhoCnOuUnidadeOrganizacional = "OU=Integrado,".trim() + "OU=Alunos".trim();
		else if (tipoUsuario.equalsIgnoreCase("superior"))
			caminhoCnOuUnidadeOrganizacional = "OU=Superior,".trim() + "OU=Alunos".trim();
		else if (tipoUsuario.equalsIgnoreCase("tae")) {
			/*
			 * Quando eram em OUS separadas para taes e docentes
			 * caminhoCnOuUnidadeOrganizacional = "OU=Taes,".trim() + "OU=Servidores".trim();
			 */
			caminhoCnOuUnidadeOrganizacional = "OU=Servidores".trim();
			
		}
		else if (tipoUsuario.equalsIgnoreCase("docente")) {
			
			/*
			 * Quando eram em OUS separadas para taes e docentes
			 * caminhoCnOuUnidadeOrganizacional = "OU=Docentes,".trim() + "OU=Servidores".trim();
			 */
			
			caminhoCnOuUnidadeOrganizacional = "OU=Servidores".trim();
			
			
		}
		else if (tipoUsuario.equalsIgnoreCase("terceirizado"))
			caminhoCnOuUnidadeOrganizacional = "OU=Tercerizados,".trim() + "OU=Servidores".trim();
		else if (tipoUsuario.equalsIgnoreCase("aluno"))
			caminhoCnOuUnidadeOrganizacional = "OU=Alunos".trim();
		else if (tipoUsuario.equalsIgnoreCase("servidor"))
			caminhoCnOuUnidadeOrganizacional = "OU=Servidores".trim();

		else
			caminhoCnOuUnidadeOrganizacional = "CN=Users".trim();
	}

	public boolean validateUser(String user, String actualPassword) {
		boolean connected = false;
		DirContext connectionValidateUser = null;
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://192.168.56.48:389/DC=IBIRUBA,DC=IFRS");
		env.put(Context.SECURITY_PRINCIPAL, user + "@IBIRUBA.IFRS");
		env.put(Context.SECURITY_CREDENTIALS, actualPassword);
		try {
			connectionValidateUser = new InitialDirContext(env);
			System.out.println("connection: " + connection);
			connected = true;
			connectionValidateUser.close();

		} catch (AuthenticationException ex) {
			System.out.println(ex.getMessage());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connected;
	}

	public DirContext newConnection() {
		if (connection != null)
			closeConnection();
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://192.168.56.48:389/DC=IBIRUBA,DC=IFRS");
		env.put(Context.SECURITY_PRINCIPAL, "Administrator@IBIRUBA.IFRS");
		env.put(Context.SECURITY_CREDENTIALS, "@lface#81");
		try {
			connection = new InitialDirContext(env);
			System.out.println("Hello World!" + connection);

		} catch (AuthenticationException ex) {
			System.out.println(ex.getMessage());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;
	}

	public String addUser(User user) {

		String answerResult = "";

		DirContext connection = newConnection();
		Attributes attributes = new BasicAttributes();
		Attribute attribute = new BasicAttribute("objectClass");
		attribute.add("User");

		attributes.put(attribute);

		// usuário
		attributes.put("samAccountName", user.getSamaccountname());

		// nome completo, preferir usar o mesmo que o do usuário
		attributes.put("cn", user.getCn());
		// Primeiro nome
		attributes.put("givenName", user.getGivenName());
		// sobrenome
		attributes.put("sn", user.getSn());
		// nome do usuário para o display
		// attributes.put("displayName", user.ge);

		// email
		if (user.getMail() != null && !user.getMail().equals(""))
			attributes.put("mail", user.getMail());
		else
			attributes.put("mail", "sememail@sememail.com");
		// password
		attributes.put("userpassword", user.getPassword());
		System.out.println("senha: " + user.getPassword());

		// some useful constants from lmaccess.h
		int UF_ACCOUNTDISABLE = 0x0002;
		int UF_PASSWD_NOTREQD = 0x0020;
		int UF_PASSWD_CANT_CHANGE = 0x0040;
		int UF_NORMAL_ACCOUNT = 0x0200;
		int UF_DONT_EXPIRE_PASSWD = 0x10200;
		int UF_PASSWORD_EXPIRED = 0x800000;
		// UF_PASSWORD_EXPIRED + UF_ACCOUNTDISABLE));
		attributes.put("userAccountControl", Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD + UF_DONT_EXPIRE_PASSWD));

		try {
			answerResult = persisteUser(user, connection, attributes);

		} catch (NameAlreadyBoundException e) {
			System.out.println("mensagem: " + e.getMessage());
			if (e.getMessage().contains("already in use")) {
				if (caminhoCnOuUnidadeOrganizacional.contains("Servidores")) {
					try {
						// tenta deletar o usuário e persistir novamente se o usuário for um servidor
						deleteUser(user);
						persisteUser(user, connection, attributes);
					} catch (NamingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
			// e.printStackTrace();
		} catch (javax.naming.directory.InvalidAttributeValueException e) {
			System.out.println("mensagem: " + e.getMessage());
			if (e.getMessage().contains("Element mail has empty attribute")) {
				attributes.put("mail", "sememail@sememail.com");
				try {
					answerResult = persisteUser(user, connection, attributes);
				} catch (NamingException e1) {
					// verificou anteriormente se o erro de persistencia foi por falta de email, aí
					// adiciona e tenta novamente
					e1.printStackTrace();
				}
			}

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return answerResult;
	}

	private String persisteUser(User user, DirContext connection, Attributes attributes) throws NamingException {
		String answerResult;
		connection.createSubcontext("CN=" + user.getCn() + "," + caminhoCnOuUnidadeOrganizacional, attributes);
		answerResult = "success";
		System.out.println(answerResult);

		System.out.println("cn: " + user.getCn() + "password user: " + user.getPassword());
		changePassword(user.getCn(), user.getPassword());
		System.out.println("Senha setada: " + user.getPassword());

		return answerResult;
	}

	public static byte[] getPasswordEnconded(String password) throws UnsupportedEncodingException {
		String newQuotedPassword = "\"" + password + "\"";
		System.out.println("password utf-16le: " + password.getBytes("UTF-16LE"));
		return newQuotedPassword.getBytes("UTF-16LE");
	}

	public boolean changePassword(String userCN, String newPassword) {
		boolean changed = false;

		DirContext connection = newConnection();
		// String userCNComplete = "CN="+userCN+","+caminhoCnOuUnidadeOrganizacional;
		try {
			System.out.println("User CN: " + userCN);
			modifyAdAttribute(userCN, "unicodePwd", getPasswordEnconded(newPassword));
			changed = true;
			System.out.println("Password changed for " + userCN);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return changed;
	}

	/*
	 * public boolean changePasswordPlainText(String userCN, String newPassword) {
	 * boolean changed = false;
	 * 
	 * DirContext connection = newConnection(); // String userCNComplete =
	 * "CN="+userCN+","+caminhoCnOuUnidadeOrganizacional; try {
	 * System.out.println("User CN: " + userCN); modifyAdAttribute(userCN,
	 * "userpassword", newPassword); changed = true;
	 * System.out.println("Password changed for " + userCN); }catch (NamingException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } return
	 * changed; }
	 */
	public void modifyAdAttribute(String userCN, String attribute, Object value) throws NamingException {
		String userCNComplete = "CN=" + userCN + "," + caminhoCnOuUnidadeOrganizacional;
		System.out.println("user Cn: " + userCNComplete);
		DirContext ldapContext = newConnection();
		ModificationItem[] modificationItem = new ModificationItem[1];
		modificationItem[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attribute, value));
		ldapContext.modifyAttributes(userCNComplete, modificationItem);
		ldapContext.close();
	}

	public void addUserToGroup(String username, String groupName) throws NamingException {
		DirContext connection = newConnection();
		ModificationItem[] mods = new ModificationItem[1];
		Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + username + ",ou=users");
		mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
		try {
			connection.modifyAttributes("cn=" + groupName + ",ou=groups", mods);
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void deleteUser(User user) throws NamingException {
		DirContext connection = newConnection();
		try {
			connection.destroySubcontext("cn=" + user.getCn() + ",ou=users");
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void deleteUserFromGroup(String username, String groupName) throws NamingException {
		DirContext connection = newConnection();
		ModificationItem[] mods = new ModificationItem[1];
		Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + username + ",ou=users,ou=system");
		mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
		try {
			connection.modifyAttributes("cn=" + groupName + ",ou=groups", mods);
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public HashMap<String, User> returnUserHashMap() {
		DirContext connection = newConnection();
		HashMap<String, User> usersHash = new HashMap<String, User>();
		try {
			connection = newConnection();
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=user)";
			NamingEnumeration values = connection.search(caminhoCnOuUnidadeOrganizacional, filter, searchCtrls);

			while (values.hasMoreElements()) {
				User user = new User();
				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();

				if (null != attribs) {
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll(); vals.hasMoreElements();) {
							Object actualEnumaration = vals.nextElement();
							// System.out.println(attributeID +": "+ actualEnumaration.toString());

							if (attributeID.trim().equals("sn"))
								user.setSn(actualEnumaration.toString());
							else if (attributeID.trim().equals("sAMAccountName".trim()))
								user.setSamaccountname(actualEnumaration.toString());
							else if (attributeID.trim().equals("mail".trim())
									&& !actualEnumaration.toString().isEmpty())
								user.setMail(actualEnumaration.toString());
							else if (attributeID.trim().equals("cn".trim()))
								user.setCn(actualEnumaration.toString());
							else if (attributeID.trim().equals("givenName".trim()))
								user.setGivenName(actualEnumaration.toString());
							else if (attributeID.trim().equals("name".trim()))
								user.setName(actualEnumaration.toString());
							else if (attributeID.trim().equals("ou".trim()))
								user.setOu(actualEnumaration.toString());
							else if (attributeID.trim().equals("userPassword".trim())) {
								System.out.println("attributeId: " + (attributeID.trim()) + " conteúdo: "
										+ actualEnumaration.toString());
								user.setPassword(actualEnumaration.toString());

							}
						}
					}
				}
				usersHash.put(user.getCn(), user);
				// System.out.println(user.toString());
			}

		} catch (NamingException e) {
			e.printStackTrace();
		}
		return usersHash;
	}

	public void listAllUser() {

		DirContext connection = newConnection();
		try {

			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=user)";
			NamingEnumeration values = connection.search(caminhoCnOuUnidadeOrganizacional, filter, searchCtrls);
			while (values.hasMoreElements()) {

				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();

				if (null != attribs) {
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll(); vals.hasMoreElements(); System.out
								.println(attributeID + ": " + vals.nextElement()))
							;
					}
				}
			}

		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void listAllUsersAndAllAttributes() {
		DirContext connection = newConnection();
		try {

			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=*)";
			NamingEnumeration values = connection.search(caminhoCnOuUnidadeOrganizacional, filter, searchCtrls);
			while (values.hasMoreElements()) {
				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();

				if (null != attribs) {
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll(); vals.hasMoreElements(); System.out
								.println(attributeID + ": " + vals.nextElement()));
					}
				}
			}

		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			connection.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void disableAccount(String userCN) throws NamingException {
		String userCNComplete = "CN=" + userCN + "," + caminhoCnOuUnidadeOrganizacional;
		
            modifyAdAttribute(userCN, "userAccountControl", "514");
       
    }
	public static void main(String[] args) throws NamingException {

		MainAdCrud app = new MainAdCrud("tae");
		app.newConnection();
		// AlterPasswordModel alterPasswordModel = new AlterPasswordModel();
		// alterPasswordModel.setUser("02378150016");
		// alterPasswordModel.setActualPassword("ifrs-123456");
		// alterPasswordModel.setNewPassword("Strike12");
		app.listAllUsersAndAllAttributes();
		// app.changePassword(alterPasswordModel.getUser().trim(),
		// alterPasswordModel.getNewPassword().trim());
		// app.changePasswordPlainText(alterPasswordModel.getUser().trim(),
		// alterPasswordModel.getNewPassword().trim());

		// app.returnUserHashMap();
		// boolean validou = app.validateUser("gustavo.paulus", "ifrs-1286428425");
		// System.out.println(validou);
		/*
		 * try { System.out.println(getPassword("teste"));
		 * 
		 * } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	public void enableAccount(String cn) {
		String userCNComplete = "CN=" + cn + "," + caminhoCnOuUnidadeOrganizacional;
		
        try {
			/* opção para apenas ativar
			modifyAdAttribute(cn, "userAccountControl", "512");
			 */
			//Valor ativar o usuário = "512" valor senha nunca expira = "66048" soma dos dois = 66560
			//opção para ativar e marcar a senha como nunca expira 66560

			modifyAdAttribute(cn, "userAccountControl", "66560");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}