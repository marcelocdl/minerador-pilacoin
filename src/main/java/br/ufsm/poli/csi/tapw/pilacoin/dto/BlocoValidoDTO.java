package br.ufsm.poli.csi.tapw.pilacoin.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonPropertyOrder(alphabetic = true)
public class BlocoValidoDTO {

    private String assinatura;
    private String chavePublica;
    private String hashPilaBloco;
    private String nonce;
    private String tipo = "BLOCO";


    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public String getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(String chavePublica) {
        this.chavePublica = chavePublica;
    }

    public String getHashPilaBloco() {
        return hashPilaBloco;
    }

    public void setHashPilaBloco(String hashPilaBloco) {
        this.hashPilaBloco = hashPilaBloco;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
