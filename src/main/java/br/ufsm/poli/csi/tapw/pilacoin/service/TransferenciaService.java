package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.dto.TransacaoDTO;
import br.ufsm.poli.csi.tapw.pilacoin.model.Transacao;
import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class TransferenciaService {

    private String enderecoServer = "srv-ceesp.proj.ufsm.br:8097";

    @SneakyThrows
    public boolean transferenciaPilacoin(TransacaoDTO transferencia){

        transferencia.setDataTransacao(new Date());

        KeyPair keyPair = Util.leKeyPair();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String transferenciaJson = objectMapper.writeValueAsString(transferencia);

        byte[] hash = Util.geraHash(transferenciaJson);

        byte[] hashAssinada = Util.geraAssinatura(hash);

        TransacaoDTO transacao = new TransacaoDTO();
        transacao.setDataTransacao(transferencia.getDataTransacao());
        transacao.setNoncePila(transferencia.getNoncePila());
        transacao.setChaveUsuarioOrigem(transferencia.getChaveUsuarioOrigem());
        transacao.setChaveUsuarioDestino(transferencia.getChaveUsuarioDestino());
        transacao.setAssinatura(Base64.encodeBase64String(hashAssinada));
        transacao.setId(0l);
        transacao.setIdBloco(0L);
        transacao.setStatus("");

        String jsonAssinado = objectMapper.writeValueAsString(transacao);

        System.out.println(jsonAssinado);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = null;


        try{

            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            enderecoServer + "/pilacoin/transfere").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(jsonAssinado);
            resp = restTemplate.exchange(requestEntity, String.class);

            if (resp.getStatusCode() == HttpStatus.OK){
                System.out.println("***PilaCoin transferido com SUCESSO***");
                return true;
            }
        }
        catch(Exception e){
            System.out.println("Erro ao transferir pilacoin: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
