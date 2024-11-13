package com.univille.graphademia.node;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class Obra {
    
    @Id @GeneratedValue 
    private Long id;
    
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
    private List<Autor> authors;
    private List<Referencia> referencias;
            
    // @Relationship(type = "CITA", direction = Relationship.Direction.OUTGOING)
    
    public Obra() {
    }

    public Obra(Referencia referencia) {
        this.paperId = referencia.getPaperId();
        this.title = referencia.getTitle();
    }
    
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

}    
