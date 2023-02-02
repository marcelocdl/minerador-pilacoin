package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URL;


public class RegistraPilacoinService {

    private String enderecoServer = "srv-ceesp.proj.ufsm.br:8097";
    //public static final String END_PILACOIN = "http://192.168.81.101:8080/pilacoin";

    public static final String END_PILACOIN = "http://srv-ceesp.proj.ufsm.br:8097/pilacoin";

    @SneakyThrows
    public boolean registraPila(String pilaCoinJson) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PilaCoin> resp = null;

        try {
            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            END_PILACOIN + "/").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(pilaCoinJson);
            resp = restTemplate.exchange(requestEntity, PilaCoin.class);
        }catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return resp.getStatusCode().equals(HttpStatus.OK);
    }

    @SneakyThrows
    public PilaCoin validaPilaCoin(BigInteger n){

        ResponseEntity<PilaCoin> response = null;
        RestTemplate restTemplate = new RestTemplate();

        PilaCoin pila = new PilaCoin();

        try {
            response = restTemplate.getForEntity(END_PILACOIN+"/?nonce="+n, PilaCoin.class);
        } catch (HttpClientErrorException e) {
            return pila;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            return null;
        }

        pila.setId(response.getBody().getId());
        pila.setChaveCriador(response.getBody().getChaveCriador());
        pila.setDataCriacao(response.getBody().getDataCriacao());
        pila.setNonce(response.getBody().getNonce());
        pila.setAssinaturaMaster(response.getBody().getAssinaturaMaster());

        return pila;

    }

}
