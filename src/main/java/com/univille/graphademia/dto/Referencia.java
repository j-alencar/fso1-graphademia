package com.univille.graphademia.dto;

import org.springframework.data.annotation.Transient;

import com.univille.graphademia.node.Obra;

public class Referencia extends Obra {
    
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
