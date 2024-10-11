package com.univille.graphademia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.univille.graphademia.node.Obra;
import com.univille.graphademia.repository.ObraRepository;

@Service
public class ObraService {

    @Autowired
    private ObraRepository obraRepository;

    public Obra salvarObra(Obra obra) {
        return obraRepository.save(obra);
    }

    public List<Obra> findByNomeAutor(String nomeAutor) {
        return obraRepository.findByNomeAutor(nomeAutor);
    }

    public List<Obra> findAll() {
        return (List<Obra>) obraRepository.findAll();
    }
}
