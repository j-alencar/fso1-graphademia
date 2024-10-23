package com.univille.graphademia;

import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.univille.graphademia.node.Obra;
import com.univille.graphademia.service.QueryApiService;

@EnableTransactionManagement
@EnableNeo4jRepositories
@SpringBootApplication
public class GraphademiaApplication {

	public static void main(String[] args) {

		//Métodos pra obra
		String nomeObra = "Uma proposta de Solução de Mineração de Dados aplicada à Segurança Pública";
		String idObra = QueryApiService.procurarObraPorTitulo(nomeObra);

		Obra obra = new Obra();
		obra.setPaperId(idObra);
		List<String> detalhesObra = QueryApiService.procurarDetalhesObra(idObra);
		obra.setReferences(detalhesObra);
		
		System.out.println(obra.getPaperId());
		System.out.println(obra.getReferences());
		
		//Métodos pro autor
		//String nomeAutor = "Edsger Dijkstra";
		//SemanticScholarAPI.procurarAutorPorNome(nomeAutor);

		//SpringApplication.run(GraphademiaApplication.class, args);
	}

}
