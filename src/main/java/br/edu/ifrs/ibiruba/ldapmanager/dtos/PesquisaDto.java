package br.edu.ifrs.ibiruba.ldapmanager.dtos;

public class PesquisaDto {
	String pesquisa;
	String status;
	String tipoServidor;
	boolean gerarCsv;

	public String getPesquisa() {
		return pesquisa;
	}

	public void setPesquisa(String pesquisa) {
		this.pesquisa = pesquisa;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTipoServidor() {
		return tipoServidor;
	}

	public void setTipoServidor(String tipoServidor) {
		this.tipoServidor = tipoServidor;
	}

	public boolean isGerarCsv() {
		return gerarCsv;
	}

	public void setGerarCsv(boolean gerarCsv) {
		this.gerarCsv = gerarCsv;
	}
	
	
	
	
}
