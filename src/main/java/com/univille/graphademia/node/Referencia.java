package com.univille.graphademia.node;

public class Referencia {
    private String paperId;
    private String title;

    public Referencia(String paperId, String title) {
        this.paperId = paperId;
        this.title = title;
    }

    public String getPaperId() {
        return paperId;
    }

    public String getTitle() {
        return title;
    }
}