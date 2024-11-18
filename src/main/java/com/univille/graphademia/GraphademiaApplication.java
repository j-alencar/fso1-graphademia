package com.univille.graphademia;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.univille.graphademia.controller.ObraController;
import com.univille.graphademia.node.Obra;
import com.univille.graphademia.service.QueryApiService;

@EnableTransactionManagement
@EnableNeo4jRepositories
@SpringBootApplication
public class GraphademiaApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(GraphademiaApplication.class, args);


        //Demo
        ObraController obraController = context.getBean(ObraController.class);

        Obra obra2 = new Obra();
        obra2.setPaperId("649def34f8be52c8b66281af98ae884c09aef38b");

        Obra obra3 = new Obra();
        obra3.setPaperId("ARXIV:2106.15928");

        List<Obra> parzinhoObra = new ArrayList<>();
        parzinhoObra.add(obra2);
        parzinhoObra.add(obra3);

        List<Obra> parzinhoDetalhes = QueryApiService.procurarDetalhesMultiplasObras(parzinhoObra);

        for (Obra obra : parzinhoDetalhes) {
            obraController.criarObra(obra);
        }
    }
}
