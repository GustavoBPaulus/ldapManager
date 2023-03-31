package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {

	

	

}
