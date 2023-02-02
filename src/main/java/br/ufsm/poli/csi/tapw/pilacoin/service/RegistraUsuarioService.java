package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.Usuario;
import br.ufsm.poli.csi.tapw.pilacoin.repository.UsuarioRepository;

import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class RegistraUsuarioService {

    @Value("srv-ceesp.proj.ufsm.br:8097")
    private String enderecoServer;

    private UsuarioRepository usuarioRepository;

    public RegistraUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostConstruct
    public void init() {
        //System.out.println("Registrado usuário: " + registraUsuario("Marcelo Costa de Lima"));
    }

    @SneakyThrows
    @Transactional
    public UsuarioRest registraUsuario(String nome) {
        KeyPair keyPair = Util.leKeyPair();
        Usuario usuarioBD = new Usuario();

        usuarioBD.setNome("Marcelo Costa de Lima");
        usuarioBD.setChaveSecreta(keyPair.getPrivate().getEncoded());
        usuarioBD.setChavePublica(keyPair.getPublic().getEncoded());

        this.usuarioRepository.save(usuarioBD);

        UsuarioRest usuarioRest = UsuarioRest.builder().nome(nome).chavePublica(keyPair.getPublic().getEncoded()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<UsuarioRest> entity = new HttpEntity<>(usuarioRest, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<UsuarioRest> resp = restTemplate.postForEntity("http://" + enderecoServer + "/usuario/", entity, UsuarioRest.class);
            UsuarioRest usu = resp.getBody();

            usuarioBD.setNome(usuarioRest.nome);
            usuarioBD.setChaveSecreta(keyPair.getPrivate().getEncoded());
            usuarioBD.setChavePublica(keyPair.getPublic().getEncoded());

            this.usuarioRepository.save(usuarioBD);
            return usu;
        } catch (Exception e) {
            System.out.println("usuario já cadastrado.");
            this.usuarioRepository.save(usuarioBD);
            String strPubKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            ResponseEntity<UsuarioRest> resp = restTemplate.postForEntity("http://" + enderecoServer + "/usuario/findByChave", new HttpEntity<>(strPubKey, headers), UsuarioRest.class);
            return resp.getBody();
        }
    }

    @SneakyThrows
    private KeyPair leKeyPair() {
        File fpub = new File("pub.key");
        File fpriv = new File("priv.key");
        if (fpub.exists() && fpriv.exists()) {
            FileInputStream pubIn = new FileInputStream(fpub);
            FileInputStream privIn = new FileInputStream(fpriv);
            byte[] barrPub = new byte[(int) pubIn.getChannel().size()];
            byte[] barrPriv = new byte[(int) privIn.getChannel().size()];
            pubIn.read(barrPub);
            privIn.read(barrPriv);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(barrPub));
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(barrPriv));
            return new KeyPair(publicKey, privateKey);
        } else {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keyPair = kpg.generateKeyPair();
            FileOutputStream pubOut = new FileOutputStream("pub.key", false);
            FileOutputStream privOut = new FileOutputStream("priv.key", false);
            pubOut.write(keyPair.getPublic().getEncoded());
            privOut.write(keyPair.getPrivate().getEncoded());
            pubOut.close();
            privOut.close();
            return keyPair;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UsuarioRest {
        private Long id;
        private byte[] chavePublica;
        private String nome;
    }

}