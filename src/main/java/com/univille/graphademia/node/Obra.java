package com.univille.graphademia.node;

import java.util.Date;
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
    private List<String> authors;
    private Date publicationDate;
    private String year;
    private List<String> externalIds;
    private String openAccessPdf;
    private List<String> publicationVenue;
    private List<String> publicationTypes;
    private Integer citationCount;
    private List<String> tldr;
    private List<String> references;

    //Relacionamento de referências bibliográficas
    // @Relationship(type = "CITA", direction = Relationship.Direction.OUTGOING)


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

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(List<String> externalIds) {
        this.externalIds = externalIds;
    }

    public String getOpenAccessPdf() {
        return openAccessPdf;
    }

    public void setOpenAccessPdf(String openAccessPdf) {
        this.openAccessPdf = openAccessPdf;
    }

    public List<String> getPublicationVenue() {
        return publicationVenue;
    }

    public void setPublicationVenue(List<String> publicationVenue) {
        this.publicationVenue = publicationVenue;
    }

    public List<String> getPublicationTypes() {
        return publicationTypes;
    }

    public void setPublicationTypes(List<String> publicationTypes) {
        this.publicationTypes = publicationTypes;
    }

    public Integer getCitationCount() {
        return citationCount;
    }

    public void setCitationCount(Integer citationCount) {
        this.citationCount = citationCount;
    }

    public List<String> getTldr() {
        return tldr;
    }

    public void setTldr(List<String> tldr) {
        this.tldr = tldr;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }
}


