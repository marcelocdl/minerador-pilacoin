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
public class PilaOutroUserDTO {

    private String assinatura;
    private byte[] chavePublica;
    private byte[] hashPilaBloco;
    private String nonce;
    private String tipo = "PILA";

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public byte[] getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(byte[] chavePublica) {
        this.chavePublica = chavePublica;
    }

    public byte[] getHashPilaBloco() {
        return hashPilaBloco;
    }

    public void setHashPilaBloco(byte[] hashPilaBloco) {
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
