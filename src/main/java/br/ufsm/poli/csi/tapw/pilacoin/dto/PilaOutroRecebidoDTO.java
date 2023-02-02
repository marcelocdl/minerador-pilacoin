package br.ufsm.poli.csi.tapw.pilacoin.dto;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PilaOutroRecebidoDTO {

    private String chaveCriador;
    private Date dataCriacao;
    private String nonce;

    public PilaOutroRecebidoDTO(Object payload) {
    }

    public PilaCoin toPilaCoin() {
        final var pilaCoin = new PilaCoin();
        pilaCoin.setDataCriacao(this.dataCriacao);
        pilaCoin.setNonce(this.nonce);
        pilaCoin.setChaveCriador(this.chaveCriador.getBytes());
        return pilaCoin;
    }

}
