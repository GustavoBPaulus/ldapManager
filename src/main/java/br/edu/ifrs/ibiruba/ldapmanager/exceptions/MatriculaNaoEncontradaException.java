package br.edu.ifrs.ibiruba.ldapmanager.exceptions;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class MatriculaNaoEncontradaException extends EntityNotFoundException {

    public MatriculaNaoEncontradaException(String matricula) {
        super(String.format("Matricula %s não encontrada", matricula));
    }

}
