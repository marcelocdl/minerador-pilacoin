package br.ufsm.poli.csi.tapw.pilacoin.miner;

import br.ufsm.poli.csi.tapw.pilacoin.service.RegistraPilacoinService;
import br.ufsm.poli.csi.tapw.pilacoin.service.WebSocketClient;
import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Random;


@Service
public class Mineracao {

    @PostConstruct
    private void init() {
        new Thread(new ThreadMiner()).start();
    }

    private static BigInteger DIFICULDADE = BigInteger.ZERO;

    public static class ThreadMiner implements Runnable{

        LocalDateTime dataAtual;

        RegistraPilacoinService registraPila = new RegistraPilacoinService();

        String textoLog = "";

        BigInteger numHash;

        int tentativas = 0;

        @Override
        @SneakyThrows
        public void run() {

            int printDif = 1;

            System.out.println("Minerando..");

            KeyPair kp = Util.leKeyPair();
            KeyPair masterPub = Util.loadMasterKey();


            while(true) {

                DIFICULDADE = WebSocketClient.sessionHandler.dificuldade;

                if(DIFICULDADE != null) {

                    if(printDif == 1){
                        System.out.println("Dificuldade de mineração: "+DIFICULDADE);
                        printDif = 0;
                    }

                    Random rnd = new SecureRandom();
                    BigInteger mNumber = new BigInteger(128, rnd).abs();

                    dataAtual = LocalDateTime.now();

                    PilaCoin pilaCoin = PilaCoin.builder()
                            .assinaturaMaster(masterPub.getPublic().getEncoded())
                            .dataCriacao(new java.util.Date())
                            .nonce(mNumber)
                            .chaveCriador(kp.getPublic().getEncoded())
                            .build();

                    // get hash
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    String pilaJson = generateJSON(pilaCoin);

                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    byte[] hash = md.digest(pilaJson.getBytes(StandardCharsets.UTF_8));

                    numHash = new BigInteger(hash).abs();

                    if (numHash.compareTo(DIFICULDADE) < 0) {
                        System.out.println("Minerou");
                        System.out.println("----------------\n");
                        System.out.println(pilaJson);
                        textoLog = pilaJson+"\n";
                        registraPila.registraPila(pilaJson);
                        salvaLog(textoLog);
                        printDif = 1;

                    } else {
                        tentativas++;
                    }
                }
            }
        }

    }

    @SneakyThrows
    private static void salvaLog(String text){
        File arquivo;
        FileWriter escritorArquivo;
        BufferedWriter escritorBuffer;

        try {
            arquivo = new File("src/logs/log.txt");

            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }

            escritorArquivo = new FileWriter(arquivo, true);
            escritorBuffer = new BufferedWriter(escritorArquivo);

            escritorBuffer.write(text);
            escritorBuffer.newLine();

            escritorBuffer.flush();
            escritorArquivo.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateJSON(PilaCoin pilaCoin) {
        String json = "";
        try {
            ObjectMapper om = new ObjectMapper();
            om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            //om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            json = om.writeValueAsString(pilaCoin);
        } catch (JsonProcessingException e) {
            System.out.println("Falha ao gerar JSON do pila coin!");
            e.printStackTrace();
        }

        if (json.equals("")) {
            throw new RuntimeException("JSON vazio!");
        }
        return json;
    }


}

/*    private static String gerarF(int n){
        StringBuilder sb = new StringBuilder();
        for (int i =0; i< n;i++){
            sb.append("f");
        }

        return sb.toString();
}

//private static final BigInteger DIFICULDADE = new BigInteger(gerarF(59), 16);


    @SneakyThrows
    public static void miner() {

        new WebSocketClient();

        BigInteger dificuldade;

        Mineracao mineracao = new Mineracao();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dataAtual;

        BigInteger tentativas = BigInteger.valueOf(0);
        String textoLog = "";

        //KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        //kpg.initialize(2048);
        //KeyPair kp = kpg.generateKeyPair();

        String caminho = "";

        KeyPair kp = mineracao.LoadKeyPair(caminho, "RSA");
        mineracao.dumpKeyPair(kp);
        System.out.println("------------------------------------------------------------------\n\n");

        while(true) {
            dificuldade = WebSocketClient.MyStompSessionHandler.getDificuldade();

            SecureRandom rnd = new SecureRandom();

            if(dificuldade != null){
                System.out.println("Dificuldade: "+dificuldade);
            }

            //webSocketClient.printDificuldade();

            dataAtual = LocalDateTime.now();

            PilaCoin pilaCoin = PilaCoin.builder()
                    .dataCriacao(new Date())
                    .chaveCriador(kp.getPublic().getEncoded())
                    .nonce(new BigInteger(128, rnd))
                    .idCriador("Marcelo").build();

            String pilaJson = new ObjectMapper().writeValueAsString(pilaCoin);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pilaJson.getBytes("UTF-8"));

            BigInteger numHash = new BigInteger(hash).abs();

            if (numHash.compareTo(DIFICULDADE) < 0) {
                System.out.println("Minerou");
                textoLog = "Minerou em "+ tentativas +" tentativas \n " +
                        "hash: "+hash.toString()+" \n" +
                        "num_hash: "+numHash+" \n" +
                        "data: "+dtf.format(dataAtual)+" \n" +
                        "-------------------------------------";
                mineracao.salvaLog(textoLog);

            } else {
                tentativas = tentativas.add(BigInteger.ONE);
            }
        }
    }
*/