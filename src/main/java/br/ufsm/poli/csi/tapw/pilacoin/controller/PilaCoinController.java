package br.ufsm.poli.csi.tapw.pilacoin.controller;

import br.ufsm.poli.csi.tapw.pilacoin.dto.ColegasDTO;
import br.ufsm.poli.csi.tapw.pilacoin.dto.TransacaoDTO;
import br.ufsm.poli.csi.tapw.pilacoin.model.*;

import br.ufsm.poli.csi.tapw.pilacoin.service.ColegasService;
import br.ufsm.poli.csi.tapw.pilacoin.service.Mineracao;
import br.ufsm.poli.csi.tapw.pilacoin.service.PilaCoinService;
import br.ufsm.poli.csi.tapw.pilacoin.service.TransferenciaService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class PilaCoinController {

    @Autowired
    Mineracao mineracao;

    @Autowired
    private ColegasService colegasService;

    @Autowired
    private PilaCoinService pilaService;

    @Autowired
    private TransferenciaService transferenciaService;

    @PostMapping("/start")
    ResponseEntity<Void> startMineracaoLoop() throws InterruptedException {
        try {
            mineracao.start();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/stop")
    ResponseEntity<Void> stopMineracao() throws InterruptedException {
        try {
            mineracao.start();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/colegas")
    public List<ColegasDTO> getColegas(){
        try{
            List<ColegasDTO> colegasList = null;
            colegasList = this.colegasService.getColegas();
            return colegasList;
        }catch (Exception e ){
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }

    @SneakyThrows
    @GetMapping("/buscarPila")
    private List<PilaCoin> getPilas(){

        List<PilaCoin> pilas = null;
        try{
            pilas = this.pilaService.getPilas();

        }catch (Exception e ){
            e.printStackTrace();
        }
        return pilas;
    }

    @SneakyThrows
    @GetMapping("/buscarNumPila")
    private int getNumPilas(){

        int num = 0;

        try{
            num = this.pilaService.getNumPilas();

        }catch (Exception e ){
            e.printStackTrace();
        }
        return num;
    }

    @SneakyThrows
    @PostMapping("/transferencia")
    public boolean getUsuarios(@RequestBody Transferencia transferencia){
        String msgResposta = "";
        transferencia.setId(new Random().nextLong());
        transferencia.setIdBloco(Long.toString(MineracaoRet.idBloco));

        try{
            try{
                if (transferencia == null){
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Body vazio"
                    );
                }else{
                    this.transferenciaService.transferenciaPilacoin(transferencia);
                }

                return true;
            }catch (Exception e ) {
                e.printStackTrace();
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, e.getMessage()
                );
            }
        }

        finally {
            return false;
        }

    }

    @MessageMapping("/log.send")
    @SendTo("/topic/public")
    public Log log(@Payload Log log){
        return log;
    }

}
