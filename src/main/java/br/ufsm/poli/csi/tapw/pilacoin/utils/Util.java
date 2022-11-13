package br.ufsm.poli.csi.tapw.pilacoin.utils;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        System.out.println(("Chave publica: " + Base64.getEncoder().encodeToString(publicKeyBytes)));

        byte[] privateKeyBytes = Files.readAllBytes(Path.of(PRIVATE_KEY_PATH));
        System.out.println(("Chave privada: " + Base64.getEncoder().encodeToString(privateKeyBytes)));

        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        KeyPair keyPair = new KeyPair(publicKey, privateKey);

        return keyPair;
    }

    public static void dumpKeyPair(KeyPair keyPair) {
        File filePublicKey = new File("" + "public.key");
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
        System.out.println(("Chave publica Master: " + Base64.getEncoder().encodeToString(publicKeyBytes)));

        byte[] privateKeyBytes = Files.readAllBytes(Path.of(PRIVATE_KEY_PATH));
        System.out.println(("Chave privada Master: " + Base64.getEncoder().encodeToString(privateKeyBytes)));

        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        KeyPair keyPair = new KeyPair(publicKey, privateKey);

        return keyPair;
    }

}
