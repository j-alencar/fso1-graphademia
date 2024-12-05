package com.univille.graphademia.node;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Area {    
    @Id @GeneratedValue 
    private Long id;

    private String nomeArea;

    @Relationship(type = "ABRANGE", direction = Relationship.Direction.OUTGOING)
    private List<Obra> obras;    

    public Area() {}

    public Area(String nomeArea) {
        this.nomeArea = nomeArea;
    }

    public String getNomeArea() {
        return nomeArea;
    }

    public void setNomeArea(String nomeArea) {
        this.nomeArea = nomeArea;
    }

    public List<Obra> getObras() {
        return obras;
    }

    public void setObras(List<Obra> obras) {
        this.obras = obras;
    }
};
