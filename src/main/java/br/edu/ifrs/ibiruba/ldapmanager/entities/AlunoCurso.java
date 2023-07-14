package br.edu.ifrs.ibiruba.ldapmanager.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class AlunoCurso {
	@Id
	private String matricula;
	@Column
	private String nomeCurso;
	@Column
	private String turmaEntrada;
	@Column
	private String curriculo;
	@Column
	private String codCurso;
	@Column
	private String statusDiscente;
	
	@ManyToOne
    @JoinColumn(name = "aluno", nullable = false)
	private Aluno aluno;
	
	public String getNomeCurso() {
		return nomeCurso;
	}
	public void setNomeCurso(String nome_curso) {
		this.nomeCurso = nome_curso;
	}
	public String getTurma_entrada() {
		return turmaEntrada;
	}
	public void setTurma_entrada(String turma_entrada) {
		this.turmaEntrada = turma_entrada;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getCurriculo() {
		return curriculo;
	}
	public void setCurriculo(String curriculo) {
		this.curriculo = curriculo;
	}
	public String getCod_curso() {
		return codCurso;
	}

	public String getStatusDiscente() {
		return statusDiscente;
	}

	public void setStatusDiscente(String statusDiscente) {
		this.statusDiscente = statusDiscente;
	}

	public Aluno getAluno() {
		return aluno;
	}
	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}
	@Override
	public int hashCode() {
		return Objects.hash(aluno, codCurso, curriculo, matricula, nomeCurso, statusDiscente, turmaEntrada);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlunoCurso other = (AlunoCurso) obj;
		return Objects.equals(aluno, other.aluno) && Objects.equals(codCurso, other.codCurso)
				&& Objects.equals(curriculo, other.curriculo) && Objects.equals(matricula, other.matricula)
				&& Objects.equals(nomeCurso, other.nomeCurso)
				&& Objects.equals(statusDiscente, other.statusDiscente)
				&& Objects.equals(turmaEntrada, other.turmaEntrada);
	}
	
	
}
