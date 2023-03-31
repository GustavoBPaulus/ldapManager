package br.edu.ifrs.ibiruba.ldapmanager.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;


@Entity
public class Servidor {
	
	@Id
	@NotNull
	@Column(unique = true)
	private String login;
	
	@Column
	private String senha;
	
	@Column
	private String senhaTemporaria;
	
	@Column
	private boolean isTemporariaAtiva;
	
	@Column
	private String data_nascimento;
	
	@Column
	@NotNull
	private String sexo;
	
	@Column
	@NotNull
	private String nome_completo;
	
	@Column
	@NotNull
	private String email;
	
	@Column(unique = true)
	@NotNull
	private String cn;
	
	@Column
	private String perfil;
	
	@Column
	private String tipoServidor;
	

	@Column
	private String status;
	
	
	@OneToMany(mappedBy = "servidor", fetch = FetchType.LAZY)
	private List<ServidorCargo> listaCargos = new ArrayList<ServidorCargo>();

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		//validar se a senha estiver nula e setar uma senha inicial
		
		this.senha = senha;
	}

	public String getData_nascimento() {
		return data_nascimento;
	}

	public void setData_nascimento(String data_nascimento) {
		this.data_nascimento = data_nascimento;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		if(login.contains(","))
			this.login = login.replace(",", "");
		this.login = login;
	}

	public List<ServidorCargo> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<ServidorCargo> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getNome_completo() {
		return nome_completo;
	}

	public void setNome_completo(String nome_completo) {
		this.nome_completo = nome_completo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public String getTipoServidor() {
		return tipoServidor;
	}

	public void setTipoServidor(String tipoServidor) {
		this.tipoServidor = tipoServidor;
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

		
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cn, data_nascimento, email, isTemporariaAtiva, listaCargos, login, nome_completo, perfil,
				senha, senhaTemporaria, sexo, tipoServidor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Servidor other = (Servidor) obj;
		return Objects.equals(cn, other.cn) && Objects.equals(data_nascimento, other.data_nascimento)
				&& Objects.equals(email, other.email) && isTemporariaAtiva == other.isTemporariaAtiva
				&& Objects.equals(listaCargos, other.listaCargos) && Objects.equals(login, other.login)
				&& Objects.equals(nome_completo, other.nome_completo) && Objects.equals(perfil, other.perfil)
				&& Objects.equals(senha, other.senha) && Objects.equals(senhaTemporaria, other.senhaTemporaria)
				&& Objects.equals(sexo, other.sexo) && Objects.equals(tipoServidor, other.tipoServidor);
	}

	@Override
	public String toString() {
		return "Servidor [login=" + login + ", senha=" + senha + ", senhaTemporaria=" + senhaTemporaria
				+ ", isTemporariaAtiva=" + isTemporariaAtiva + ", data_nascimento=" + data_nascimento + ", sexo=" + sexo
				+ ", nome_completo=" + nome_completo + ", email=" + email + ", cn=" + cn + ", perfil=" + perfil
				+ ", tipoServidor=" + tipoServidor + ", listaCargos=" + listaCargos + "]";
	}

	

}
