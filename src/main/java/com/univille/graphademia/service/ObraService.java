package com.univille.graphademia.service;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.univille.graphademia.node.Autor;
import com.univille.graphademia.node.Obra;
import com.univille.graphademia.repository.ObraRepository;

@Service
public class ObraService {

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private AutorService autorService;

    public Obra salvarObra(Obra obra) {
        for (Autor autor : obra.getAuthors()) {
            if (autor.getObras() == null) {
                autor.setObras(new ArrayList<>());
            }
            autor.getObras().add(obra);
            autorService.salvarAutor(autor);
        }
        return obraRepository.save(obra);
    }
}


