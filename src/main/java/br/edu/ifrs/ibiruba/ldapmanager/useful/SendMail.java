package br.edu.ifrs.ibiruba.ldapmanager.useful;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.edu.ifrs.ibiruba.ldapmanager.entities.MessageModel;


public class SendMail {
	
	Logger logger = LoggerFactory.getLogger(SendMail.class);

	private Properties returnProperties() {
		Properties props = new Properties();
		/** Parâmetros de conexão com servidor Gmail */
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		return props;
	}

	private Session returnSession(Properties props) {
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("timanager@ibiruba.ifrs.edu.br", "@lface#236");
			}
		});

		/** Ativa Debug para sessão */
		session.setDebug(true);
		return session;
	}

	private boolean sendMail(Session session, Address[] toUser, MessageModel messageModel) {
		boolean success = false;

		try {

			Message message = new MimeMessage(session);
			// Remetente
			message.setFrom(new InternetAddress("timanager@ibiruba.ifrs.edu.br"));

			message.setRecipients(Message.RecipientType.TO, toUser);
			message.setSubject(messageModel.getSubject());// Assunto
			message.setText(messageModel.getText());
			/** Método para enviar a mensagem criada */
			Transport.send(message);

			success = true;
			//System.out.println("Feito!!!");

		} catch (MessagingException e) {
			logger.error("erro no método sendMail: "+ e.getStackTrace());
			throw new RuntimeException(e);
		}
		return success;
	}

	public boolean sendMailLogic(MessageModel message) {

		Properties props = returnProperties();

		Session session = returnSession(props);
		
		//destinations format "seuamigo@gmail.com, seucolega@hotmail.com, seuparente@yahoo.com.br"
		Address[] toUser = null;
		try {
			toUser = InternetAddress.parse(message.getDestination());
		} catch (AddressException e) {
			//System.out.println("erro ao transformar usário");
			logger.error("erro no método sendMailLogic: "+ e.getStackTrace());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sendMail(session, toUser, message);

	}

	public static void main(String[] args) {

	}
}