package br.edu.ifrs.ibiruba.ldapmanager.exceptions;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ServidorNaoEncontradoException extends EntityNotFoundException {

    public ServidorNaoEncontradoException(String cpf) {
        super(String.format("Servidor com o cpf %s não encontrado", cpf));
    }

}
