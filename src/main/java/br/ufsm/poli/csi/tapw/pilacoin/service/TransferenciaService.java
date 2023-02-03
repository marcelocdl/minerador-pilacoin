package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.dto.TransacaoDTO;
import br.ufsm.poli.csi.tapw.pilacoin.model.MineracaoRet;
import br.ufsm.poli.csi.tapw.pilacoin.model.Transacao;
import br.ufsm.poli.csi.tapw.pilacoin.model.Transferencia;
import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import java.net.URL;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Service
public class TransferenciaService {

    private String enderecoServer = "http://srv-ceesp.proj.ufsm.br:8097";

    @SneakyThrows
    public boolean transferenciaPilacoin(Transferencia transferencia){

        transferencia.setDataTransacao(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Calendar.getInstance().getTime()));

        KeyPair keyPair = Util.leKeyPair();

        Long id = 0L;

        while (id<1){
            id = new Random().nextLong();
        }

        Transferencia transacao = new Transferencia();
        transacao.setDataTransacao(transferencia.getDataTransacao());
        transacao.setNoncePila(transferencia.getNoncePila());
        transacao.setChaveUsuarioOrigem(transferencia.getChaveUsuarioOrigem());
        transacao.setChaveUsuarioDestino(transferencia.getChaveUsuarioDestino());

        transacao.setIdBloco(Long.toString(MineracaoRet.idBloco));
        transacao.setStatus(null);

        String transferenciaJson = Util.geraJson(transacao);

        transacao.setAssinatura(Util.generateSignature(transferenciaJson));

        String jsonFinal = Util.geraJson(transacao);

        System.out.println(jsonFinal);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = null;

        try{

            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            enderecoServer + "/pilacoin/transfere").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(jsonFinal);
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
