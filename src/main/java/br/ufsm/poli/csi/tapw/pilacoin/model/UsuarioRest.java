package br.ufsm.poli.csi.tapw.pilacoin.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRest {
    private Long id;
    private byte[] chavePubByte;
    private String chavePublica;
    private String nome;
}