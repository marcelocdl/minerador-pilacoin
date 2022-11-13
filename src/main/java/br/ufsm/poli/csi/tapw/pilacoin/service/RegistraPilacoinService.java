package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.model.UsuarioRest;
import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.security.KeyPair;
import java.util.Base64;

public class RegistraPilacoinService {

    private String enderecoServer = "srv-ceesp.proj.ufsm.br:8097";
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
            e.printStackTrace();
        }
        return resp.getStatusCode().equals(HttpStatus.OK);
    }
}
