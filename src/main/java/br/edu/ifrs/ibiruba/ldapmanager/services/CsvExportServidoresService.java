package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Servidor;

@Service
public class CsvExportServidoresService {
	//Logger logger = (Logger) LoggerFactory.getLogger(CsvExportService.class);
	  
		@Autowired
	    ServidorCrudService servidorCrudService;

	

	    public void writeEmployeesToCsv(Writer writer,  List<Servidor> servidores) {


	        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
	            csvPrinter.printRecord("login", "data_nascimento", "email","nome_completo", "sexo", "cn", "perfil", "tipo_servidor", "status");
	            for (Servidor servidor : servidores) {
	                csvPrinter.printRecord(servidor.getLogin(), servidor.getData_nascimento(), servidor.getEmail(), servidor.getNome_completo(), servidor.getSexo(), servidor.getCn(), servidor.getPerfil(), servidor.getTipoServidor(), 
	                		servidor.getStatus()
	                	);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
}
