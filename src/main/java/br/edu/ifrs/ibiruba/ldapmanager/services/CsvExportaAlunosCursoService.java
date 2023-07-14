package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ifrs.ibiruba.ldapmanager.entities.Aluno;
import br.edu.ifrs.ibiruba.ldapmanager.entities.AlunoCurso;


@Service
public class CsvExportaAlunosCursoService {
	//Logger logger = (Logger) LoggerFactory.getLogger(CsvExportService.class);
	  
		@Autowired
	    ServidorCrudService servidorCrudService;

	

	    public void exportMatriculasToCsv(Writer writer,  List<AlunoCurso> alunos) {


	        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
	            csvPrinter.printRecord("matricula","login", "data_nascimento", "email","nome_completo", "sexo", "tipo_aluno", "status");
	            for (AlunoCurso aluno : alunos) {
	                csvPrinter.printRecord(aluno.getMatricula(),aluno.getAluno().getLogin(), aluno.getAluno().getData_nascimento(), aluno.getAluno().getEmail(), aluno.getAluno().getNome_completo(), aluno.getAluno().getSexo(), aluno.getAluno().getTipoAluno(), aluno.getAluno().getStatus());
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
}
