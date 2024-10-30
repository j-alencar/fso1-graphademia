package com.univille.graphademia;

import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.univille.graphademia.node.Obra;
import com.univille.graphademia.node.Referencia;
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
		
		List<Referencia> referencias = QueryApiService.procurarDetalhesObra(idObra);
		
		for (Referencia ref : referencias) {
			System.out.println(ref.getPaperId());
			System.out.println(ref.getTitle());
		}
		
		//Métodos pro autor
		//String nomeAutor = "Edsger Dijkstra";
		//SemanticScholarAPI.procurarAutorPorNome(nomeAutor);

		//SpringApplication.run(GraphademiaApplication.class, args);
	}

}
