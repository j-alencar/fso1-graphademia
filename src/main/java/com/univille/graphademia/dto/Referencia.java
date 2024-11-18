package com.univille.graphademia.dto;

import org.springframework.data.annotation.Transient;

public class Referencia {
    
    @Transient
    private final String paperId;
    @Transient
    private final String title;

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
