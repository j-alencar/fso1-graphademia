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
    public Long id;

    private String authorId;
    private String name;
    private String dblp;
    private String orcid;
    private Integer hindex;

    public Autor(){
    };

    @Relationship(type = "ESCREVE")
    private List<Obra> obras = new ArrayList<>(); // Inicializar obras aqui

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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDblp() {
        return dblp;
    }

    public void setDblp(String dblp) {
        this.dblp = dblp;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public Integer getHindex() {
        return hindex;
    }

    public void setHindex(Integer hindex) {
        this.hindex = hindex;
    }
}
