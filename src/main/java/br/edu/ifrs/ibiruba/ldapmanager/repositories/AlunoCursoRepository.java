package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;


@Repository
public interface AlunoCursoRepository extends JpaRepository<AlunoCurso, String>{

}
