package br.ufsm.poli.csi.tapw.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class BlocoValidado {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private Long numeroBloco;
    private String chaveUsuarioMinerador;
    private String nonce;
    private String nonceBlocoAnterior;
    @OneToMany(mappedBy = "bloco")
    private List<Transacao> transacoes;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroBloco() {
        return numeroBloco;
    }

    public void setNumeroBloco(Long numeroBloco) {
        this.numeroBloco = numeroBloco;
    }

    public String getChaveUsuarioMinerador() {
        return chaveUsuarioMinerador;
    }

    public void setChaveUsuarioMinerador(String chaveUsuarioMinerador) {
        this.chaveUsuarioMinerador = chaveUsuarioMinerador;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonceBlocoAnterior() {
        return nonceBlocoAnterior;
    }

    public void setNonceBlocoAnterior(String nonceBlocoAnterior) {
        this.nonceBlocoAnterior = nonceBlocoAnterior;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
}
