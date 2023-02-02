package br.ufsm.poli.csi.tapw.pilacoin.dto;

import br.ufsm.poli.csi.tapw.pilacoin.model.Transacao;
import lombok.Data;

import java.util.List;

@Data
public class BlocoDTO {
    private String chaveUsuarioMinerador;
    private String nonce;
    private String nonceBlocoAnterior;
    private Long numeroBloco;
    private List<TransacaoDTO> transacoes;
}
