package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.dto.*;
import br.ufsm.poli.csi.tapw.pilacoin.model.*;
import br.ufsm.poli.csi.tapw.pilacoin.repository.UsuarioRepository;
import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

public class BlocoService {

    public static KeyPair keyPair;
    public static final String END_SERVER = "http://srv-ceesp.proj.ufsm.br:8097";


    @SneakyThrows
    public static void mineraBloco(Bloco b) {

        BigInteger DIFICULDADE;

        KeyPair kp = Util.leKeyPair();


        SecureRandom sr = new SecureRandom();
        String jsonBloco;

        BlocoDTO bloco = buscaBloco(b.getNumeroBloco());

        bloco.setNumeroBloco(b.getNumeroBloco());
        bloco.setChaveUsuarioMinerador(Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));
        bloco.setNonceBlocoAnterior(Long.toString(b.getNonce()));


        /*BlocoValidado blocoValidado = new BlocoValidado();
        blocoValidado.setNonceBlocoAnterior(bloco.getNonceBlocoAnterior());
        blocoValidado.setNumeroBloco(bloco.getNumeroBloco());
        blocoValidado.setTransacoes(bloco.getTransacoes());
        blocoValidado.setChaveUsuarioMinerador(Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));*/

        ObjectMapper objectMapper = new ObjectMapper();

        while (true) {

            DIFICULDADE = MineracaoRet.DIFICULDADE;

            BigInteger nonce = new BigInteger(128, sr);
            bloco.setNonce(nonce.toString());

            objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
            jsonBloco = objectMapper.writeValueAsString(bloco);

            byte[] hash = Util.geraHash(jsonBloco);
            BigInteger numHash = new BigInteger(hash).abs();


            if (numHash.compareTo(DIFICULDADE) < 0) {
                boolean ret = BlocoService.registraBloco(jsonBloco);

                if (ret == true){
                    System.out.println("%%%%% BLOCO MINERADO %%%%%");
                    break;
                }
            }
        }
    }

    @SneakyThrows
    public static boolean registraBloco(String jsonBloco) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = null;

        try {
            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            END_SERVER + "/bloco/").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(jsonBloco);
            resp = restTemplate.exchange(requestEntity, String.class);

            System.out.println("Resp -> "+resp.getBody());

        }catch (RuntimeException e) {
            System.out.println("Resp -> "+resp.getBody());
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        System.out.println("Resp -> "+resp.getBody());
        return true;

    }

    public static BlocoDTO buscaBloco(Long numeroBloco){

        ResponseEntity<BlocoDTO> response = null;
        RestTemplate restTemplate = new RestTemplate();

        //System.out.println("Busca bloco->"+numeroBloco);

        BlocoDTO bloco = new BlocoDTO();

        try {
            response = restTemplate.getForEntity(END_SERVER+"/bloco/?numBloco="+numeroBloco, BlocoDTO.class);
        } catch (HttpClientErrorException e) {
            return bloco;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            return null;
        }

        return response.getBody();
    }

    public static boolean validaBloco(BlocoOutroRecebidoDTO b){
        if(MineracaoRet.DIFICULDADE !=null){

            String blocoOutroUserJson = Util.geraJson(b);

            byte[] hash = Util.geraHash(blocoOutroUserJson);

            BlocoValidoDTO bloco = new BlocoValidoDTO();
            bloco.setNonce(b.getNonce());
            bloco.setChavePublica(b.getChaveCriador());
            bloco.setHashPilaBloco(Base64.getEncoder().encodeToString(hash));

            BigInteger numHash = new BigInteger(hash).abs();

            if(numHash.compareTo(MineracaoRet.DIFICULDADE) < 0){
                boolean retorno = postBlocoValidado(bloco, hash);

                if(retorno){
                    System.out.println("-- BLOCO RECEBIDO É VÁLIDO --");
                    System.out.println("NONCE -> "+bloco.getNonce());
                    System.out.println("----------------------------");
                }

                return retorno;
            }

        }

        return false;
    }

    @SneakyThrows
    public static boolean postBlocoValidado(BlocoValidoDTO blocoValidoDTO, byte[] hash){
        KeyPair kp = Util.leKeyPair();

        BlocoValidoDTO bloco = new BlocoValidoDTO();
        bloco.setChavePublica(Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));
        bloco.setHashPilaBloco(Base64.getEncoder().encodeToString(hash));
        bloco.setTipo("BLOCO");
        bloco.setNonce(blocoValidoDTO.getNonce());

        String blocoJsonOutroUser = Util.geraJson(bloco);

        byte[] hashBloco = Util.geraHash(blocoJsonOutroUser);

        byte[] hashAssinada = Util.geraAssinatura(hashBloco);

        bloco.setAssinatura(org.apache.tomcat.util.codec.binary.Base64.encodeBase64String(hashAssinada));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String blocoJsonFinal = objectMapper.writeValueAsString(bloco);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = null;

        try{

            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            "http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/bloco/validaBlocoOutroUsuario").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(blocoJsonFinal);
            resp = restTemplate.exchange(requestEntity, String.class);

            if (resp.getStatusCode() == HttpStatus.OK){
                return true;
            }
        }
        catch(Exception e){
            System.out.println("Erro ao validar bloco: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

}
