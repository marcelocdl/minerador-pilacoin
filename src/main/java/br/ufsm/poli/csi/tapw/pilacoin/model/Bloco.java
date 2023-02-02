package br.ufsm.poli.csi.tapw.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonPropertyOrder(alphabetic = true)
@Table (name = "bloco")
public class Bloco {

    @Id
    @GeneratedValue
    private Long id;
    private Long nonce;

    private String chaveUsuarioMinerador;

    private Long numeroBloco;
    private String nonceBlocoAnterior;
    @OneToMany(mappedBy = "bloco")
    private List<Transacao> transacoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public String getChaveUsuarioMinerador() {
        return chaveUsuarioMinerador;
    }

    public void setChaveUsuarioMinerador(String chaveUsuarioMinerador) {
        this.chaveUsuarioMinerador = chaveUsuarioMinerador;
    }

    public Long getNumeroBloco() {
        return numeroBloco;
    }

    public void setNumeroBloco(Long numeroBloco) {
        this.numeroBloco = numeroBloco;
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

