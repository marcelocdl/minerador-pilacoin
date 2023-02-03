package br.ufsm.poli.csi.tapw.pilacoin.model;

import br.ufsm.poli.csi.tapw.pilacoin.dto.PilaOutroRecebidoDTO;
import lombok.Data;

import java.math.BigInteger;

@Data
public class MineracaoRet {

    public static BigInteger DIFICULDADE;
    public static PilaOutroRecebidoDTO PILA_RECEBIDO;

    public static long idBloco;

    public static boolean mining = false;

    public static BigInteger getDIFICULDADE() {
        return DIFICULDADE;
    }

    public static void setDIFICULDADE(BigInteger DIFICULDADE) {
        MineracaoRet.DIFICULDADE = DIFICULDADE;
    }

    public static PilaOutroRecebidoDTO getPilaRecebido() {
        return PILA_RECEBIDO;
    }

    public static void setPilaRecebido(PilaOutroRecebidoDTO pilaRecebido) {
        PILA_RECEBIDO = pilaRecebido;
    }


}
