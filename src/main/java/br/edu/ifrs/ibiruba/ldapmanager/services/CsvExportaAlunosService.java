package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;


@Service
public class CsvExportaAlunosService {
	//Logger logger = (Logger) LoggerFactory.getLogger(CsvExportService.class);
	  
		@Autowired
	    ServidorCrudService servidorCrudService;

	

	    public void writeEmployeesToCsv(Writer writer,  List<Aluno> alunos) {


	        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
	            csvPrinter.printRecord("login", "data_nascimento", "email","nome_completo", "sexo", "tipo_aluno", "status");
	            for (Aluno aluno : alunos) {
	                csvPrinter.printRecord(aluno.getLogin(), aluno.getData_nascimento(), aluno.getEmail(), aluno.getNome_completo(), aluno.getSexo(), aluno.getTipoAluno(), aluno.getStatus());
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
}
