package com.univille.graphademia.service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.univille.graphademia.node.Autor;
import com.univille.graphademia.repository.AutorRepository;

@Service("AutorService")
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public Autor salvarAutor(Autor autor) {
        return autorRepository.save(autor);
    }

    public List<Autor> buscarTodosAutores() {
        return StreamSupport.stream(autorRepository.findAll().spliterator(), false)
                            .collect(Collectors.toList()); // Converte iterable p/ lista
    }

    public Autor buscarAutorPorId(Long id) {
        return autorRepository.findById(id).orElse(null);
    }

    public Autor atualizarAutor(Long id, Autor autorAtualizado) {
        return autorRepository.findById(id).map(autorExistente -> {
            autorExistente.setName(autorAtualizado.getName());
            autorExistente.setOrcid(autorAtualizado.getOrcid());
            autorExistente.setDblp(autorAtualizado.getDblp());
            autorExistente.setHindex(autorAtualizado.getHindex());
            return autorRepository.save(autorExistente);
        }).orElse(null);
    }

    public boolean deletarAutor(Long id) {
        Optional<Autor> autorExistente = autorRepository.findById(id);
        if (autorExistente.isPresent()) {
            autorRepository.deleteById(id);
            return true;
        }
        return false;
    }
};

