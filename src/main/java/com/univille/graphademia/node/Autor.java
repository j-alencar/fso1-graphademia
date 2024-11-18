package com.univille.graphademia.node;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
@Node
public class Autor {

    @Id
    @GeneratedValue
    private Long uuid;

    private String authorId;
    private String name;

    @Relationship(type = "ESCREVE")
    private List<Obra> obras = new ArrayList<>(); // Inicializar aqui

    public Autor(String authorId, String name) {
        this.authorId = authorId;
        this.name = name;
        this.obras = new ArrayList<>(); // Ou aqui também serve
    }

    public List<Obra> getObras() {
        return obras;
    }

    public void setObras(List<Obra> obras) {
        this.obras = obras;
    }
}
