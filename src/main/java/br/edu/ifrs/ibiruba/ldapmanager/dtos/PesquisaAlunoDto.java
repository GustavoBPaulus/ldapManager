package br.edu.ifrs.ibiruba.ldapmanager.dtos;

public class PesquisaAlunoDto {
	String pesquisa;
	String status;
	String tipoAluno;

	String curso;
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


	public String getTipoAluno() {
		return tipoAluno;
	}

	public void setTipoAluno(String tipoAluno) {
		this.tipoAluno = tipoAluno;
	}

	public boolean isGerarCsv() {
		return gerarCsv;
	}

	public void setGerarCsv(boolean gerarCsv) {
		this.gerarCsv = gerarCsv;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}
}
