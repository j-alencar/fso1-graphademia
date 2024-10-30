package com.univille.graphademia.service;
import java.util.List;

import com.univille.graphademia.node.Obra;

public class RespostaApiService {
    private List<Obra> data;  
    
    public List<Obra> getData() {
        return data;
    }

    public void setData(List<Obra> data) {
        this.data = data;
    }
}
