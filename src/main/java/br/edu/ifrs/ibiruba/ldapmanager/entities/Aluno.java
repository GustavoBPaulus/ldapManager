package br.edu.ifrs.ibiruba.ldapmanager.entities;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Aluno {
	
	@Id
	private String login;
	
	@Column
	private String nome_completo;
	
	@Column
	private String senha;
	
	@Column
	private String senhaTemporaria;
	
	@Column
	private boolean isTemporariaAtiva;
	
	@Column
	private String email;
	
	@Column
	private String data_nascimento;
	
	@Column
	private String sexo;

	@Column
	private String tipoAluno;
	
	@OneToMany(mappedBy = "aluno")
	private List<AlunoCurso> listaCursosAluno;
	
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		//validar se a senha estiver nula e setar uma senha inicial
		
		this.login = login;
	}
	public String getNome_completo() {
		return nome_completo;
	}
	public void setNome_completo(String nome_completo) {
		this.nome_completo = nome_completo;
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
	@Override
	public int hashCode() {
		return Objects.hash(data_nascimento, email, isTemporariaAtiva, listaCursosAluno, login, nome_completo, senha,
				senhaTemporaria, sexo, tipoAluno);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Aluno other = (Aluno) obj;
		return Objects.equals(data_nascimento, other.data_nascimento) && Objects.equals(email, other.email)
				&& isTemporariaAtiva == other.isTemporariaAtiva
				&& Objects.equals(listaCursosAluno, other.listaCursosAluno) && Objects.equals(login, other.login)
				&& Objects.equals(nome_completo, other.nome_completo) && Objects.equals(senha, other.senha)
				&& Objects.equals(senhaTemporaria, other.senhaTemporaria) && Objects.equals(sexo, other.sexo)
				&& Objects.equals(tipoAluno, other.tipoAluno);
	}
	
	
	

	
	

	
}
