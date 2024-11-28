package com.univille.graphademia.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.univille.graphademia.node.Autor;
import com.univille.graphademia.repository.AutorRepository;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public Autor salvarAutor(Autor autor) {
        return autorRepository.save(autor);
    }
    
};
