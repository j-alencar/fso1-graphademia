package com.univille.graphademia;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.univille.graphademia.api.SemanticScholarAPI;

@EnableTransactionManagement
@EnableNeo4jRepositories
@SpringBootApplication
public class GraphademiaApplication {

	public static void main(String[] args) {

		//Métodos pra obra
		String nomeObra = "Uma proposta de Solução de Mineração de Dados aplicada à Segurança Pública";
		String idObra = SemanticScholarAPI.procurarObraPorTitulo(nomeObra);
		SemanticScholarAPI.procurarReferenciasDaObra(idObra);
		//"TODO: Formatar títulos com acento/cedilha, mostrar de forma tabular p/ confirmar antes do POST";
		
		//Métodos pro autor
		System.out.println("\n");
		String nomeAutor = "Edsger Dijkstra";
		SemanticScholarAPI.procurarAutorPorNome(nomeAutor);
		//TODO: Mostrar IDs do autor de forma tabular pro usuário identificar, talvez mais dados pra ajudar

		//SpringApplication.run(GraphademiaApplication.class, args);
	}

}
