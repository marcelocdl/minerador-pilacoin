package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.dto.BlocoOutroRecebidoDTO;
import br.ufsm.poli.csi.tapw.pilacoin.dto.PilaOutroRecebidoDTO;
import br.ufsm.poli.csi.tapw.pilacoin.model.Bloco;
import br.ufsm.poli.csi.tapw.pilacoin.model.DificuldadeRet;
import br.ufsm.poli.csi.tapw.pilacoin.model.MineracaoRet;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.utils.Util;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Objects;

@Service
public class WebSocketClient {

    @Getter
    public static StompSessionHandlerImpl sessionHandler = new StompSessionHandlerImpl();
    //@Value("${server.address}")
    private String serverUrl = "srv-ceesp.proj.ufsm.br:8097";

    //private String serverUrl = "192.168.81.101:8080";

    static ValidaPilaOutroUsuarioService validaService = new ValidaPilaOutroUsuarioService();

    static BlocoService blocoService = new BlocoService();

    private final WebSocketService webSocketServerService;

    public WebSocketClient(WebSocketService webSocketServerService) {
        this.webSocketServerService = webSocketServerService;
    }

    @PostConstruct
    public void init() {
        String webSocketAddress = "ws://" + serverUrl + "/websocket/websocket";

        System.out.println("Iniciando...");
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        stomp.connect(webSocketAddress, sessionHandler);
    }


    @Scheduled(fixedRate = 10000)
    public void printDificuldade() {
        if (sessionHandler.dificuldade != null) {
            System.out.println("Dificuldade Atual: " + sessionHandler.dificuldade);
        }
    }

    public static class StompSessionHandlerImpl implements StompSessionHandler {

        private final String endPointDif = "/topic/dificuldade";

        private final String endPointValida = "/topic/validaMineracao";
        private final String endPointEncotraBloco = "/topic/descobrirNovoBloco";

        private final String endPointValidaBloco = "/topic/validaBloco";

        @Getter
        public BigInteger dificuldade;

        @Getter
        public PilaCoin pilaCoin;


        public PilaOutroRecebidoDTO pilaRecebido;

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(endPointDif, this);
            session.subscribe(endPointValida, this);
            session.subscribe(endPointEncotraBloco, this);
            session.subscribe(endPointValidaBloco, this);
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            if (Objects.equals(headers.getDestination(), endPointDif)) {
                return DificuldadeRet.class;
            } else if (Objects.equals(headers.getDestination(), endPointValida)) {
                return PilaCoin.class;
            } else if (Objects.equals(headers.getDestination(), endPointEncotraBloco)) {
                return Bloco.class;
            } else if (Objects.equals(headers.getDestination(), endPointValidaBloco)) {
                return BlocoOutroRecebidoDTO.class;
            }

            return null;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            assert payload != null;

            if(payload.getClass().equals(DificuldadeRet.class)) {
                dificuldade = new BigInteger(((DificuldadeRet) payload).getDificuldade(), 16);
                MineracaoRet.DIFICULDADE = dificuldade;
                handleDificuldade(dificuldade);
            }else if(payload.getClass().equals(PilaCoin.class)) {
                System.out.println(" ## Chegou pila para validar ## ");

                PilaCoin pila = PilaCoin.builder()
                        .dataCriacao(((PilaCoin) payload).getDataCriacao())
                        .nonce(((PilaCoin) payload).getNonce())
                        .chaveCriador(((PilaCoin) payload).getChaveCriador())
                        .build();

                handleValidacaoPila(pila);
            }else if(payload.getClass().equals(Bloco.class)){
                System.out.println("$$ Novo bloco para minerar $$");
                handleBlocoParaMinerar(payload);
            }else if (payload.getClass().equals(BlocoOutroRecebidoDTO.class)){
                System.out.println("$$$ Novo bloco recebido para validar $$$");

                BlocoOutroRecebidoDTO bloco = BlocoOutroRecebidoDTO.builder()
                        .dataCriacao(((BlocoOutroRecebidoDTO) payload).getDataCriacao())
                        .nonce(((BlocoOutroRecebidoDTO) payload).getNonce())
                        .chaveCriador(((BlocoOutroRecebidoDTO) payload).getChaveCriador())
                        .build();

                handleValidacaoBloco(bloco);
            }

        }

        @SneakyThrows
        private void handleDificuldade(Object o)  {
            dificuldade = new BigInteger(((DificuldadeRet) o).getDificuldade(), 16);
            if (!Objects.equals(MineracaoRet.DIFICULDADE, dificuldade)) {
                System.out.println("Dificuldade: "+dificuldade);
            }

            MineracaoRet.DIFICULDADE = dificuldade;
        }

        @SneakyThrows
        private void handleValidacaoPila(Object o)  {
            PilaCoin pila = (PilaCoin) o;
            MineracaoRet.PILA_RECEBIDO = pilaRecebido;
            validaService.validaPila(pila);
        }

        @SneakyThrows
        private void handleBlocoParaMinerar(Object o)  {
            Bloco bloco = (Bloco) o;
            MineracaoRet.idBloco = bloco.getNumeroBloco();
            blocoService.mineraBloco(bloco);

            //validaService.validaPila(bloco);
        }

        private void handleValidacaoBloco(Object o) {
            BlocoOutroRecebidoDTO bloco = (BlocoOutroRecebidoDTO) o;
            blocoService.validaBloco(bloco);
        }


    }



}