package br.ufsm.poli.csi.tapw.pilacoin.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;

    private String permissao;
    private String token;
}