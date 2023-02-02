package br.ufsm.poli.csi.tapw.pilacoin.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "colegas")
public class Colegas {

    @Id
    private Long id;
    private byte[] chavePublica;
    private String nome;

}