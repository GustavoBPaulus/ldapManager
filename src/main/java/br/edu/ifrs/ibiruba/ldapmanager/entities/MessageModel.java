package br.edu.ifrs.ibiruba.ldapmanager.entities;

public class MessageModel {
	private String remetent;
	//destinations format "seuamigo@gmail.com, seucolega@hotmail.com, seuparente@yahoo.com.br"
	private String destination;
	private String subject;
	private String text;
	
	public String getRemetent() {
		return remetent;
	}
	public void setRemetent(String remetent) {
		this.remetent = remetent;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
