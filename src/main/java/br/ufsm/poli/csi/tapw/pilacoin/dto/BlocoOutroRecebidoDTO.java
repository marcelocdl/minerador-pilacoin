package br.ufsm.poli.csi.tapw.pilacoin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlocoOutroRecebidoDTO {

    private String chaveCriador;
    private Date dataCriacao;
    private String nonce;

    public BlocoOutroRecebidoDTO(Object payload) {
    }


}
