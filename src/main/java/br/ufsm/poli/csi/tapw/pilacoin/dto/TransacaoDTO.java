package br.ufsm.poli.csi.tapw.pilacoin.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TransacaoDTO {
    private String assinatura;
    private String chaveUsuarioDestino;
    private String chaveUsuarioOrigem;
    private Date dataTransacao;
    private Long id;
    private Long idBloco;
    private String noncePila;
    private String status;


}