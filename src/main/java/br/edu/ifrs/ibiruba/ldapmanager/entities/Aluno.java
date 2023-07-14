package br.edu.ifrs.ibiruba.ldapmanager.entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Aluno {

	@Id
	private String login;

	@Column(name = "nome_completo")
	private String nomeCompleto;

	@Column
	private String senha;

	@Column
	private String senhaTemporaria;

	@Column
	private boolean isTemporariaAtiva;

	@Column
	private String email;

	@Column(name = "dataNascimento")
	private String data_nascimento;

	@Column
	private String sexo;

	@Column
	private String tipoAluno;

	@Column
	private String status;

	@OneToMany(mappedBy = "aluno")
	private List<AlunoCurso> listaCursosAluno;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		// validar se a senha estiver nula e setar uma senha inicial

		this.login = login;
	}

	public String getNome_completo() {
		return nomeCompleto;
	}

	public void setNome_completo(String nome_completo) {
		this.nomeCompleto = nome_completo;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getData_nascimento() {
		return data_nascimento;
	}

	public void setData_nascimento(String data_nascimento) {
		this.data_nascimento = data_nascimento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public List<AlunoCurso> getListaCursosAluno() {
		return listaCursosAluno;
	}

	public void setListaCursosAluno(List<AlunoCurso> listaCursosAluno) {
		this.listaCursosAluno = listaCursosAluno;
	}

	public String getSenhaTemporaria() {
		return senhaTemporaria;
	}

	public void setSenhaTemporaria(String senhaTemporaria) {
		this.senhaTemporaria = senhaTemporaria;
	}

	public boolean isTemporariaAtiva() {
		return isTemporariaAtiva;
	}

	public void setTemporariaAtiva(boolean isTemporariaAtiva) {
		this.isTemporariaAtiva = isTemporariaAtiva;
	}

	public String getTipoAluno() {
		return tipoAluno;
	}

	public void setTipoAluno(String tipoAluno) {
		this.tipoAluno = tipoAluno;
	}

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
