package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.dto.PilaOutroUserDTO;
import br.ufsm.poli.csi.tapw.pilacoin.model.MineracaoRet;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;

import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyPair;

@Service
public class ValidaPilaOutroUsuarioService {

    public static final String END_VALIDA_PILACOIN = "http://srv-ceesp.proj.ufsm.br:8097";

    RegistraPilacoinService registraPila = new RegistraPilacoinService();
    MineracaoRet mineracao = new MineracaoRet();

    KeyPair kp = Util.leKeyPair();

    @SneakyThrows
    public boolean validaPila(PilaCoin pilaCoin){

        if(MineracaoRet.DIFICULDADE !=null){

            PilaCoin pila = PilaCoin.builder()
                    .dataCriacao(pilaCoin.getDataCriacao())
                    .chaveCriador(pilaCoin.getChaveCriador())
                    .nonce(pilaCoin.getNonce()).build();

            String pilaOutroUserJson = Util.geraJson(pila);

            byte[] hash = Util.geraHash(pilaOutroUserJson);

            BigInteger numHash = new BigInteger(hash).abs();

            if(numHash.compareTo(MineracaoRet.DIFICULDADE) < 0){
                boolean retorno = postPilaValidado(pila, hash);

                if(retorno){
                    System.out.println("-- PILA RECEBIDO É VÁLIDO --");
                    System.out.println("NONCE -> "+pila.getNonce());
                    System.out.println("----------------------------");
                }

                return retorno;
            }

        }

        return false;
    }

    @SneakyThrows
    public boolean postPilaValidado(PilaCoin pilaCoin, byte[] hash){
        PilaOutroUserDTO pilaOutroUser = new PilaOutroUserDTO();
        pilaOutroUser.setChavePublica(kp.getPublic().getEncoded());
        pilaOutroUser.setHashPilaBloco(hash);
        pilaOutroUser.setTipo("PILA");
        pilaOutroUser.setNonce(pilaCoin.getNonce());

        String pilaJsonOutroUser = Util.geraJson(pilaOutroUser);

        byte[] hashPila = Util.geraHash(pilaJsonOutroUser);

        byte[] hashAssinada = Util.geraAssinatura(hashPila);

        pilaOutroUser.setAssinatura(Base64.encodeBase64String(hashAssinada));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String pilaJsonFinal = objectMapper.writeValueAsString(pilaOutroUser);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = null;

        try{

            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            "http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/pilacoin/validaPilaOutroUsuario").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(pilaJsonFinal);
            resp = restTemplate.exchange(requestEntity, String.class);

            if (resp.getStatusCode() == HttpStatus.OK){
                System.out.println("Json pila validado->"+pilaJsonFinal);
                return true;
            }
        }
        catch(Exception e){
            System.out.println("Erro ao validar pila: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

}
