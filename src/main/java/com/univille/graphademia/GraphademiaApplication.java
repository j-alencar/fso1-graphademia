package com.univille.graphademia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.awt.Desktop;
import java.net.URI;

@EnableTransactionManagement
@EnableNeo4jRepositories
@SpringBootApplication
public class GraphademiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphademiaApplication.class, args);
        System.out.println("Rodando!");

        abrirNavegador("http://localhost:8080/obras/pesquisar-obra");
    }

    private static void abrirNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
