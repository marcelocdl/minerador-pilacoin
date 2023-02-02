package br.ufsm.poli.csi.tapw.pilacoin.dto;

public class ColegasDTO {

    int id;
    String chavePublica;
    String nome;

    public ColegasDTO() {
    }

    public ColegasDTO(int id, String chavePublica, String nome) {
        this.id = id;
        this.chavePublica = chavePublica;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(String chavePublica) {
        this.chavePublica = chavePublica;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
