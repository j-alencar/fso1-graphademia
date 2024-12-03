package com.univille.graphademia.node;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.univille.graphademia.dto.Referencia;

@Node

public class Obra {    
    @Id @GeneratedValue 
    public Long id;
    
    private String paperId;
    private String title;
    private Integer year;
    private String doi;
    private String publicationVenueName;
    private String publicationVenueType;
    private String url;
    private String tldr;
    private String publicationTypes;
    private String publicationDate;
    
    @Transient
    private List<Autor> authors;

    @Transient
    private List<Referencia> referencias;

    @Relationship(type = "ABRANGE", direction = Relationship.Direction.INCOMING)
    private List<Area> fieldsOfStudy;

    @Relationship(type = "RECOMENDA")
    private List<Obra> recomendacoes;

    @Relationship(type = "CITA")
    private List<Obra> obrasReferenciadas;
                
    public Obra() {
    }

    public Obra(Obra obra) {
        this.paperId = obra.getPaperId();
        this.title = obra.getTitle();
    }

    public void gerarObrasDeObras(List<? extends Object> listaObras) {
        if (listaObras == null || listaObras.isEmpty()) {
            return;
        }
        
        if (obrasReferenciadas == null) {
            obrasReferenciadas = new ArrayList<>(); 
        }
    
        if (recomendacoes == null) {
            recomendacoes = new ArrayList<>(); 
        }
    
        for (Object item : listaObras) {
            if (item instanceof Referencia referencia && referencia.getPaperId() != null) {
                obrasReferenciadas.add(new Obra(referencia));
            } else if (item instanceof Obra recomendacao && recomendacao.getPaperId() != null) {
                recomendacoes.add(new Obra(recomendacao));
            }
        }
    };

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getPublicationVenueName() {
        return publicationVenueName;
    }

    public void setPublicationVenueName(String publicationVenueName) {
        this.publicationVenueName = publicationVenueName;
    }

    public String getPublicationVenueType() {
        return publicationVenueType;
    }

    public void setPublicationVenueType(String publicationVenueType) {
        this.publicationVenueType = publicationVenueType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTldr() {
        return tldr;
    }

    public void setTldr(String tldr) {
        this.tldr = tldr;
    }

    public String getPublicationTypes() {
        return publicationTypes;
    }

    public void setPublicationTypes(String publicationTypes) {
        this.publicationTypes = publicationTypes;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<Autor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Autor> authors) {
        this.authors = authors;
    }

    public List<Referencia> getReferencias() {
        return referencias;
    }
    
    public void setReferencias(List<Referencia> referencias) {
        this.referencias = referencias;
    }

    public List<Obra> getObrasReferenciadas() {
        return obrasReferenciadas;
    }

    public void setObrasReferenciadas(List<Obra> obrasReferenciadas) {
        this.obrasReferenciadas = obrasReferenciadas;
    }

    public List<Area> getAreas() {
        return fieldsOfStudy;
    }

    public void setAreas(List<Area> fieldsOfStudy) {
        this.fieldsOfStudy = fieldsOfStudy;
    }
    public List<Obra> getRecomendacoes() {
        return recomendacoes;
    }

    public void setRecomendacoes(List<Obra> recomendacoes) {
        this.recomendacoes = recomendacoes;
    }
};
