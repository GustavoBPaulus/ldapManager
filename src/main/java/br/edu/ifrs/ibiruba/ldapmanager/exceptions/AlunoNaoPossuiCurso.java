package br.edu.ifrs.ibiruba.ldapmanager.exceptions;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AlunoNaoPossuiCurso extends EntityNotFoundException {

    public AlunoNaoPossuiCurso(String cpf) {
        super(String.format("Aluno com o cpf %s não possui curso", cpf));
    }

}
