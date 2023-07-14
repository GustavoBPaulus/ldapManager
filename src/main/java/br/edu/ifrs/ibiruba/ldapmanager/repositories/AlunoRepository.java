package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;


@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {

	@Query(value = "SELECT * FROM aluno WHERE nome_completo LIKE %:nome%", nativeQuery = true)
	List<Aluno> findByNomeCompletoContaining(String nome);

	List<Aluno> findByStatus(String status);

	List<Aluno> findByTipoAluno(String tipoAluno);

	List<Aluno> findByNomeCompletoContainingAndStatusEquals(String nomeCompleto, String status);

	List<Aluno> findByNomeCompletoContainingAndTipoAlunoEquals(String nome, String tipoDeAluno);


	List<Aluno> findByStatusEqualsAndTipoAlunoEquals(String status, String tipoDeAluno);

	List<Aluno> findByNomeCompletoContainingAndStatusEqualsAndTipoAlunoEquals(String nome, String status,
			String tipoDeAluno);

	Optional<Aluno> findByLogin(String cpf);

	Optional<Aluno> findByEmail(String email);

	@Query(value = "select distinct tipo_aluno from aluno", nativeQuery = true )
	List<String> listAllTiposDeAluno();

	@Query(value = "select distinct nome_curso from aluno_curso", nativeQuery = true )
	List<String> listAllCursos();

	Optional<Aluno> findByNomeCompleto(String nomeCompleto);



}
