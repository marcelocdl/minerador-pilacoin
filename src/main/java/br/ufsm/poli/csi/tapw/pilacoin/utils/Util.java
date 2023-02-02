package br.ufsm.poli.csi.tapw.pilacoin.utils;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class Util {

    private static final String PUBLIC_KEY_PATH = "src/main/resources/public.txt";
    private static final String PRIVATE_KEY_PATH = "src/main/resources/private.txt";
    private static final String MASTER_KEY_PATH = "src/main/resources/master-pub.key";

    @SneakyThrows
    public static KeyPair leKeyPair() {
        byte[] publicKeyBytes = Files.readAllBytes(Path.of(PUBLIC_KEY_PATH));
        //System.out.println(("Chave publica: " + Base64.getEncoder().encodeToString(publicKeyBytes)));

        byte[] privateKeyBytes = Files.readAllBytes(Path.of(PRIVATE_KEY_PATH));
        //System.out.println(("Chave privada: " + Base64.getEncoder().encodeToString(privateKeyBytes)));

        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        KeyPair keyPair = new KeyPair(publicKey, privateKey);

        return keyPair;
    }

    public static void dumpKeyPair(KeyPair keyPair) {
        File filePublicKey = new File(PUBLIC_KEY_PATH);
        byte[] encodedPub = new byte[(int) filePublicKey.length()];
        System.out.println("Minha Public Key: " + Base64.getEncoder().encodeToString(encodedPub));

        //PrivateKey priv = keyPair.getPrivate();
        //System.out.println("Private Key: " + getHexString(priv.getEncoded()));
    }

    @SneakyThrows
    public static KeyPair loadMasterKey()
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        byte[] publicKeyBytes = Files.readAllBytes(Path.of(MASTER_KEY_PATH));
        //System.out.println(("Chave publica Master: " + Base64.getEncoder().encodeToString(publicKeyBytes)));

        byte[] privateKeyBytes = Files.readAllBytes(Path.of(PRIVATE_KEY_PATH));
        //System.out.println(("Chave privada Master: " + Base64.getEncoder().encodeToString(privateKeyBytes)));

        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        KeyPair keyPair = new KeyPair(publicKey, privateKey);

        return keyPair;
    }


    public static byte[] geraHash(String json) {
        byte[] hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(json.getBytes(StandardCharsets.UTF_8));
        }catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        if (hash == null) {
            throw new RuntimeException("Não gerou hash ---> hash está vazia.");
        }

        return hash;
    }

    @SneakyThrows
    public static byte[] geraAssinatura(byte[] hash) {
        KeyPair kp = leKeyPair();

        Cipher cipherRSA = Cipher.getInstance("RSA");
        cipherRSA.init(Cipher.ENCRYPT_MODE, kp.getPrivate());

        byte[] hashAssinatura = cipherRSA.doFinal(hash);

        return hashAssinatura;
    }

    public static String geraJson(Object o) {
        String json = "";
        try {
            ObjectMapper om = new ObjectMapper();
            om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            json = om.writeValueAsString(o);
        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }

        if (json.equals("")) {
            throw new RuntimeException("Erro gerar JSON ---> JSON vazio");
        }
        return json;
    }

    @SneakyThrows
    public static byte[] geraHashBytes(PilaCoin pila) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String pilaJson = geraJson(pila);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(pilaJson.getBytes(StandardCharsets.UTF_8));

        return hash;
    }

}
