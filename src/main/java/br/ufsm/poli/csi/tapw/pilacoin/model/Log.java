package br.ufsm.poli.csi.tapw.pilacoin.model;

import lombok.Builder;
import lombok.Getter;

import java.awt.*;

@Builder
public class Log {

    public Log() {
    }

    public Log(String content) {
        this.content = content;
    }

    public Log(String content, String time) {
        this.content = content;
        this.time = time;
    }

    @Getter
    private String content;
    @Getter
    private String time;
}
