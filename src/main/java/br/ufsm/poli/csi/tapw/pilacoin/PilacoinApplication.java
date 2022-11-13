package br.ufsm.poli.csi.tapw.pilacoin;

import br.ufsm.poli.csi.tapw.pilacoin.miner.Mineracao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class PilacoinApplication {

	public static void main(String[] args) {
		SpringApplication.run(PilacoinApplication.class, args);

		//Thread th = new Thread(new Mineracao.ThreadMiner());
		//th.start();
	}

}
