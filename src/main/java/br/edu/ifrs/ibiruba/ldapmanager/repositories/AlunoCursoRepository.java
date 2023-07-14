package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;


@Repository
public interface AlunoCursoRepository extends JpaRepository<AlunoCurso, String>{

	List<AlunoCurso> findByAluno(Aluno aluno);

	Optional<AlunoCurso> findByMatricula(String matricula);
	
	List<AlunoCurso> findByNomeCurso (String nome_curso);
	
	List<AlunoCurso> findByStatusDiscente(String statusDiscente);

	List<AlunoCurso> findByAlunoNomeCompletoContaining(String lowerCase);




	@Query(value= "SELECT aluno_curso.*\n" +
			"FROM aluno_curso\n" +
			"JOIN aluno ON aluno_curso.aluno = aluno.login\n" +
			"WHERE aluno.nome_completo LIKE  %:nome%\n" +
			"  AND aluno_curso.status_discente = :status\n" +
			"  AND aluno_curso.nome_curso = :nomeCurso", nativeQuery = true)
	List<AlunoCurso> findByAlunoNomeCompletoContainingAndStatusDiscenteEqualsAndNomeCursoEquals(String nome,
			String status, String nomeCurso);

	List<AlunoCurso> findByStatusDiscenteEqualsAndNomeCursoEquals(String upperCase, String curso);

	List<AlunoCurso> findByAlunoNomeCompletoContainingAndNomeCursoEquals(String nome, String curso);

	List<AlunoCurso> findByAlunoNomeCompletoContainingAndStatusDiscenteEquals(String lowerCase, String trim);


	List<AlunoCurso> findBynomeCurso(String trim);
}
