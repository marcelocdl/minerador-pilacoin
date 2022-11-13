package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.DificuldadeRet;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Objects;

@Service
public class WebSocketClient {

    @Getter
    public static final StompSessionHandlerImpl sessionHandler = new StompSessionHandlerImpl();
    //@Value("${server.address}")
    private String serverUrl = "srv-ceesp.proj.ufsm.br:8097";

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

        private final String endPoint = "/topic/dificuldade";

        @Getter
        public BigInteger dificuldade;

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(endPoint, this);
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Objects.equals(headers.getDestination(), endPoint) ? DificuldadeRet.class : null;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            assert payload != null;
            dificuldade = new BigInteger(((DificuldadeRet) payload).getDificuldade(), 16);
        }


    }



}