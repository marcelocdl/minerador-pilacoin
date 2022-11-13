package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.UsuarioRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegistraUsuarioService {

    private static final String PUBLIC_KEY_PATH = "src/main/resources/public.txt";
    private static final String PRIVATE_KEY_PATH = "src/main/resources/private.txt";
    private static final String MASTER_KEY_PATH = "src/main/resources/master-pub.key";

    public static final String REQ_USER = "http://srv-ceesp.proj.ufsm.br:8097/usuario";

    //@Value("${endereco.server}")
    private String enderecoServer = "srv-ceesp.proj.ufsm.br:8097/usuario";

    @PostConstruct
    public void init() {
        //System.out.println("Registrando usuário: " + registraUsuario("Marcelo Costa de Lima"));
    }

    @SneakyThrows
    public Boolean registraUsuario(String nome) {
        KeyPair keyPair = leKeyPair();

        byte[] publicKeyBytes = Files.readAllBytes(Path.of(PUBLIC_KEY_PATH));
        UsuarioRest user = new UsuarioRest();

        user.setNome("Marcelo Costa de Lima");
        user.setChavePublica(Base64.getEncoder().encodeToString(publicKeyBytes));

        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put("id", 0);
        mapUser.put("nome", user.getNome());
        mapUser.put("chavePublica", user.getChavePublica());

        System.out.println("#########################\n");
        System.out.println("Chave: "+user.getChavePublica());
        System.out.println("#########################\n");

        String req = new ObjectMapper().writeValueAsString(mapUser);

        ResponseEntity<UsuarioRest> response = null;
        RestTemplate restTemplate = new RestTemplate();

        //UsuarioRest usuarioRest = UsuarioRest.builder().nome(nome).chavePublica(Base64.getEncoder().encodeToString(publicKeyBytes).getBytes()).build();
        //HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + accessToken);

        try {
            String json = new ObjectMapper().writeValueAsString(mapUser);
            RequestEntity<String> requestEntity = RequestEntity.post(new URL(REQ_USER + "/").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(json);
            response = restTemplate.exchange(requestEntity, UsuarioRest.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                e.printStackTrace();
                return true;
            }
            System.out.println("Falha ao criar usuario! " + e.getStatusCode());
            e.printStackTrace();
        }
        if (response != null && response.getBody() != null) {
            System.out.println(response.getBody());
            if(response.getStatusCode() == HttpStatus.ACCEPTED || response.getStatusCode() == HttpStatus.CREATED) {
                    System.out.println("Usuario Cadastrado com sucesso!");
                    return true;
                }
            } else {
                return false;
            }
            return false;

    }

    @SneakyThrows
    private KeyPair leKeyPair() {
        byte[] publicKeyBytes = Files.readAllBytes(Path.of(PUBLIC_KEY_PATH));
        //System.out.println(("Chave publica Base64: " + Base64.getEncoder().encodeToString(publicKeyBytes)));

        byte[] privateKeyBytes = Files.readAllBytes(Path.of(PRIVATE_KEY_PATH));
        //System.out.println(("Chave privada Base64: " + Base64.getEncoder().encodeToString(privateKeyBytes)));

        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        KeyPair keyPair = new KeyPair(publicKey, privateKey);

        return keyPair;
    }

}
