package br.edu.ifrs.ibiruba.ldapmanager.entities;

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
	private String nome_curso;
	@Column
	private String turma_entrada;
	@Column
	private String curriculo;
	@Column
	private String cod_curso;
	@Column
	private String status_discente;
	
	@ManyToOne
    @JoinColumn(name = "aluno", nullable = false)
	private Aluno aluno;
	
	public String getNome_curso() {
		return nome_curso;
	}
	public void setNome_curso(String nome_curso) {
		this.nome_curso = nome_curso;
	}
	public String getTurma_entrada() {
		return turma_entrada;
	}
	public void setTurma_entrada(String turma_entrada) {
		this.turma_entrada = turma_entrada;
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
		return cod_curso;
	}
	public void setCod_curso(String cod_curso) {
		this.cod_curso = cod_curso;
	}
	public String getStatus_discente() {
		return status_discente;
	}
	public void setStatus_discente(String status_discente) {
		this.status_discente = status_discente;
	}
	public Aluno getAluno() {
		return aluno;
	}
	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}
	
	
}
