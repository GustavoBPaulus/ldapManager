package br.edu.ifrs.ibiruba.ldapmanager.exceptions;

public class ServidorPossuiCargoException extends RuntimeException {

    public ServidorPossuiCargoException(String cn) {
        super(String.format("Servidor %s possui cargo(s) realacionado(s)", cn));
    }

}
