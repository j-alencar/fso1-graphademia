package com.univille.graphademia;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.univille.graphademia.node.Autor;
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
		
		Obra obra1 = QueryApiService.procurarDetalhesObra(idObra);

		for (Autor autor : obra1.getAuthors()){
			System.out.println(autor.getName());
		}
		
		//Métodos pro autor
		var authors = QueryApiService.procurarAutorPorNome("Edsger Dijkstra");
		System.out.println(authors);
		//SpringApplication.run(GraphademiaApplication.class, args);
	}

}
