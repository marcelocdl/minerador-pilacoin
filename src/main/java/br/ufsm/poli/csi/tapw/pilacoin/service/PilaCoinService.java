package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PilaCoinService {

    @SneakyThrows
    public List<PilaCoin> getPilas(){
        List<PilaCoin> pilas = null;
        List<PilaCoin> meusPilas = new ArrayList<>();

        KeyPair keyPair = Util.leKeyPair();

        ResponseEntity<String> resp = null;
        RestTemplate restTemplate = new RestTemplate();

        try {

        }catch (Exception e ){
            e.printStackTrace();
        }

        resp= restTemplate.getForEntity("http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/pilacoin/all",String.class);

        if (resp.getStatusCode() == HttpStatus.OK){
            ObjectMapper mapper = new ObjectMapper();
            pilas  = Arrays.asList( mapper.readValue(resp.getBody(), PilaCoin[].class));

            for (PilaCoin p: pilas) {
                String key =  Base64.encodeBase64String(keyPair.getPublic().getEncoded());
                String base64 = Base64.encodeBase64String(p.getChaveCriador());
                if(base64.equals(key)){
                    meusPilas.add(p);
                }
            }
        }
        return meusPilas;
    }
}
