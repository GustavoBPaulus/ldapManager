package br.edu.ifrs.ibiruba.ldapmanager.exceptions;

public class MatriculaAtivaException extends RuntimeException {

    public MatriculaAtivaException(String matricula) {
        super(String.format("Matricula %s está ativa", matricula));
    }

}
