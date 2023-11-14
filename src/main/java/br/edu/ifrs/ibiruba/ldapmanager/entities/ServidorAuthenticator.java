package br.edu.ifrs.ibiruba.ldapmanager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "servidor_authenticator", schema = "authenticator")
public class ServidorAuthenticator {
    @Id
    String cn;
    @Column(nullable = false, name = "senha_bcrypt")
    String senhaBcrypt;
    @Column (nullable = false)
    String nome;
    @Column(nullable = false)
    String status;

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getSenhaBcrypt() {
        return senhaBcrypt;
    }

    public void setSenhaBcrypt(String senhaBcrypt) {
        this.senhaBcrypt = senhaBcrypt;
    }

    public String getNome() {
        return nome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServidorAuthenticator that = (ServidorAuthenticator) o;
        return Objects.equals(cn, that.cn) && Objects.equals(senhaBcrypt, that.senhaBcrypt) && Objects.equals(nome, that.nome) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cn, senhaBcrypt, nome, status);
    }
}
