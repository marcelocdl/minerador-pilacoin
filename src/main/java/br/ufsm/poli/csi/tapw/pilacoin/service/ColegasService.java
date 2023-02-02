package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.dto.ColegasDTO;
import br.ufsm.poli.csi.tapw.pilacoin.model.Colegas;
import br.ufsm.poli.csi.tapw.pilacoin.repository.ColegasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class ColegasService {

    public static final String END_SERVER = "http://srv-ceesp.proj.ufsm.br:8097";


    @Autowired
    ColegasRepository colegasRepository;

    @PostConstruct
    public List<Colegas> getColegas() {

        RestTemplate restTemplate = new RestTemplate();

        List<Colegas> retorno = new ArrayList<>();

        List<ColegasDTO> colegas = new ArrayList<>();

        List<Colegas> oldColegas = colegasRepository.findAll();

        ResponseEntity<List<ColegasDTO>> resp = restTemplate.exchange(
                END_SERVER+"/usuario/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ColegasDTO>>() {});

        if(resp != null && resp.hasBody()){
            colegas = resp.getBody();
        } else if (resp == null && resp.hasBody() == false) {
            return null;
        }

        for (ColegasDTO c: colegas ) {
            Colegas col = new Colegas();
            col.setId((long) c.getId());
            col.setChavePublica(c.getChavePublica().getBytes());
            col.setNome(c.getNome());

            retorno.add(col);
        }

        if(retorno.containsAll(oldColegas)){

        }
        else colegasRepository.saveAll(retorno);

        return retorno;

    }

}
