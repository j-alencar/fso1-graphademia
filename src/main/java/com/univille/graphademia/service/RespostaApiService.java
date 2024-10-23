package com.univille.graphademia.service;
import java.util.List;

import com.univille.graphademia.node.Obra;

public class RespostaApiService {
    private List<Obra> dados;

    public List<Obra> getDados() {
        return dados;
    }

    public void setDados(List<Obra> dados) {
        this.dados = dados;
    }
}