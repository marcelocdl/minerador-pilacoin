package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.MineracaoRet;
import br.ufsm.poli.csi.tapw.pilacoin.repository.PilaCoinRepository;

import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


@Service
public class Mineracao {

    @Autowired
    private PilaCoinRepository pilaCoinRepository;

    private MineracaoRet mineracao;

    Boolean minerar = false;

    public static final Logger LOG = Logger.getLogger(Mineracao.class.getName());

    public void miningLoop() throws InterruptedException {
        while (MineracaoRet.DIFICULDADE == null) {
            Thread.sleep(500);
        }
        Thread.yield();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(initMineracao());
        System.out.println("Mineração Iniciada!");
    }

    private Thread initMineracao(){

        return new Thread(() -> {
            BigInteger DIFICULDADE;

            RegistraPilacoinService registraPila = new RegistraPilacoinService();

            String textoLog = "";

            BigInteger numHash = BigInteger.ZERO;

            byte[] hash;

            String pilaJson;

            KeyPair kp = Util.leKeyPair();

            while (MineracaoRet.mining == true) {

                DIFICULDADE = mineracao.DIFICULDADE;

                if (DIFICULDADE != null) {

                    Random rnd = new SecureRandom();
                    BigInteger mNumber = new BigInteger(128, rnd).abs();

                    PilaCoin pilaCoin = PilaCoin.builder()
                            .dataCriacao(new java.util.Date())
                            .nonce(mNumber.toString())
                            .chaveCriador(kp.getPublic().getEncoded())
                            .build();

                    // get hash e json
                    hash = Util.geraHashBytes(pilaCoin);
                    pilaJson = Util.geraJson(pilaCoin);

                    numHash = new BigInteger(hash).abs();

                    if (numHash.compareTo(DIFICULDADE) < 0) {
                        System.out.println("########### Minerou ##########\nNonce --> "+mNumber.toString());
                        registraPila.registraPila(pilaJson);

                        //textoLog = registraPila.validaPilaCoin(mNumber);

                        PilaCoin pilaResp = registraPila.validaPilaCoin(mNumber);

                        if (pilaResp != null) {
                            System.out.println("-------- PILACOIN REGISTRADO E VALIDADO COM SUCESSO ------\n");
                            //textoLog = textoLog + "  numHash: "+numHash+"   hash:"+hash+"\n";
                            //salvaLog(textoLog);
                            byte[] hashTeste = Util.geraHashBytes(pilaResp);
                            BigInteger numHashTeste = new BigInteger(hash).abs();

                            this.pilaCoinRepository.save(pilaResp);

                        } else {
                            System.out.println("@@@ ERRO AO REGISTRAR PILACOIN @@@");
                        }

                    }
                }
            }
        });
    }


    public void start() throws InterruptedException {
        MineracaoRet.mining = !MineracaoRet.mining;
        if (MineracaoRet.mining) {
            System.out.println("Valor -> "+MineracaoRet.mining);
            miningLoop();
        }else {
            System.out.println("Valor -> "+MineracaoRet.mining+" ==== MINERAÇÃO PARADA!!");
        }
    }

}


/*    @SneakyThrows
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
*/