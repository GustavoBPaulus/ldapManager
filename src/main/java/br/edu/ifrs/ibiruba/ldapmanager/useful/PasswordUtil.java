package br.edu.ifrs.ibiruba.ldapmanager.useful;

import java.util.Random;

public class PasswordUtil {

    public static String retornaSenhaTemporaria(){
        Random gerador = new Random();

        // Gera uma senha aleatória
        return  gerador.nextInt(9999) + "@ibiruba.ifrs";

    }
}
