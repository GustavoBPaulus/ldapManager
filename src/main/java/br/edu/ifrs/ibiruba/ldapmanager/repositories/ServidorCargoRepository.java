package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;
import br.edu.ifrs.ibiruba.ldapmanager.entities.ServidorCargo;


@Repository
public interface ServidorCargoRepository extends JpaRepository<ServidorCargo, String> {

List<ServidorCargo> findByServidor(Servidor servidor);	
	
}
