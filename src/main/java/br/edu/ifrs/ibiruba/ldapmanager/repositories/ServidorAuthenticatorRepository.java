package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import br.edu.ifrs.ibiruba.ldapmanager.entities.ServidorAuthenticator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServidorAuthenticatorRepository extends JpaRepository<ServidorAuthenticator, String> {



}
