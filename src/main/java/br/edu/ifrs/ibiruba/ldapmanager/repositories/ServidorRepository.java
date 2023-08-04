package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;


@Repository
public interface ServidorRepository extends JpaRepository<Servidor, String>{
		
	Optional<Servidor> findBycn(String cn);

	Optional<Servidor> findByLogin(String cpf);
	 
	@Query("select DISTINCT(s.perfil) from Servidor s")
	List<String> selectDistinctPerfis();

	Optional<Servidor> findByEmail(String email);

	List<Servidor> findBycnContaining(String cn);
	
	@Query(value = "SELECT distinct s.*\n"
			+ "FROM servidor s \n"
			+ "INNER JOIN  servidor_cargo sc \n"
			+ "ON s.login = sc.servidor_id_fk\n"
			+ "where sc.status = :status", nativeQuery = true)
	List<Servidor> findBytatusIsEqual(String status);
	
	@Query(value = "SELECT * \n"
			+ "FROM servidor s \n"
			+ "where s.tipo_servidor = :tipoServidor", nativeQuery = true)
	List<Servidor> findByTipoServidor(String tipoServidor);
	
	@Query(value = "SELECT distinct s.*\n"
			+ "FROM servidor s \n"
			+ "INNER JOIN  servidor_cargo sc \n"
			+ "ON s.login = sc.servidor_id_fk\n"
			+ "where s.cn like %:cn% and sc.status = :status", nativeQuery = true)
	List<Servidor> findBycnContainingAndStatusIsEqual(String cn, String status);

	
	@Query(value = "SELECT distinct s.*\n"
			+ "FROM servidor s \n"
			+ "INNER JOIN  servidor_cargo sc \n"
			+ "ON s.login = sc.servidor_id_fk\n"
			+ "where s.cn like %:cn% and sc.status = :status and s.tipo_servidor = :tipoServidor", nativeQuery = true)
	List<Servidor> findBycnContainingAndStatusAndTipoAreEqual(String cn, String status, String tipoServidor);
	
	@Query(value = "SELECT *\n"
			+ "FROM servidor s \n"
			+ "where s.cn like %:cn% and s.tipo_servidor = :tipoServidor", nativeQuery = true)
	List<Servidor> findBycnContainingAndTipo(String cn,  String tipoServidor);
	
	@Query(value = "SELECT distinct s.*\n"
			+ "FROM servidor s \n"
			+ "INNER JOIN  servidor_cargo sc \n"
			+ "ON s.login = sc.servidor_id_fk\n"
			+ "where sc.status = :status and s.tipo_servidor = :tipoServidor", nativeQuery = true)
	List<Servidor> findByStatusAndTipo(String status,  String tipoServidor);

	@Query(value = "select * from servidor where cn = :cn and tipo_servidor = :tipoServidor", nativeQuery = true)
	List<Servidor> findByCnEqualsAndTipoServidorEquals(String cn, String tipoServidor);

	Optional<Servidor> findByCn(String cn);
}
