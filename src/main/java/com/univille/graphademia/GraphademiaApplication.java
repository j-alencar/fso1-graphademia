package com.univille.graphademia;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.univille.graphademia.node.Obra;
import com.univille.graphademia.service.QueryApiService;
import java.util.ArrayList;
import java.util.List;

@EnableTransactionManagement
@EnableNeo4jRepositories
@SpringBootApplication
public class GraphademiaApplication {

    public static void main(String[] args) {

        // Métodos pra obra
        // String nomeObra = "Uma proposta de Solução de Mineração de Dados aplicada à Segurança Pública";
        // String idObra = QueryApiService.procurarObraPorTitulo(nomeObra);

        // Obra obra1 = QueryApiService.procurarDetalhesObra(idObra);
        // System.out.println("Obra: " + obra1.getTitle() + " (" + obra1.getYear() + ")");

        // List<Obra> obrasFromReferencias = new ArrayList<>();

        // // Converter cada referência da obra1 em uma Obra
        // if (obra1.getReferencias() != null) {
        //     for (var referencia : obra1.getReferencias()) {
        //         Obra obraFromRef = new Obra(referencia); 
		// 		obrasFromReferencias.add(obraFromRef);
        //         System.out.println("Obra a partir de referência: " + obraFromRef.getTitle() + " (" + obraFromRef.getPaperId() + ")");
        //     }
    	// }

        //

        Obra obra2 = new Obra();
        obra2.setPaperId("649def34f8be52c8b66281af98ae884c09aef38b");
        System.out.println(obra2);
        Obra obra3= new Obra();
        obra3.setPaperId("ARXIV:2106.15928");
        List<Obra> parzinhoObra = new ArrayList<>(); 
        parzinhoObra.add(obra2);
        parzinhoObra.add(obra3);
        List<Obra> parzinhoDetalhes = QueryApiService.procurarDetalhesMultiplasObras(parzinhoObra);


        for (Obra obra : parzinhoDetalhes) {
            System.out.println("Autores: " + obra.getAuthors());
            System.out.println("Referências: " + obra.getReferencias());
            System.out.println("URL: " + obra.getUrl());
            System.out.println("Ano: " + obra.getYear());
        }

		// Métodos pro autor
		// var authors = QueryApiService.procurarAutorPorNome("Edsger Dijkstra");
		// System.out.println(authors);
		// SpringApplication.run(GraphademiaApplication.class, args);
	}
}