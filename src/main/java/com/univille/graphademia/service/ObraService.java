package com.univille.graphademia.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.univille.graphademia.node.Autor;
import com.univille.graphademia.node.Obra;
import com.univille.graphademia.repository.ObraRepository;

@Service("ObraService")
public class ObraService {

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private AutorService autorService;

    public List<Obra> buscarTodasObras() {
        return (List<Obra>) obraRepository.findAll();
    }

    public Obra buscarPorId(Long id) {
        return obraRepository.findById(id).orElse(null);
    }

    public Obra salvarObra(Obra obra) {
        if (obra.getAuthors() != null) {
            for (Autor autor : obra.getAuthors()) {
                if (autor.getObras() == null) {
                    autor.setObras(new ArrayList<>());
                }
                autor.getObras().add(obra);
                autorService.salvarAutor(autor);
            }
        }
        return obraRepository.save(obra);
    }

    public Obra atualizarObra(Long id, Obra obraAtualizada) {
        Obra obraExistente = obraRepository.findById(id).orElse(null);
        if (obraExistente != null) {
            obraExistente.setTitle(obraAtualizada.getTitle());
            obraExistente.setDoi(obraAtualizada.getDoi());
            obraExistente.setPublicationVenueName(obraAtualizada.getPublicationVenueName());
            obraExistente.setPublicationVenueType(obraAtualizada.getPublicationVenueType());
            obraExistente.setUrl(obraAtualizada.getUrl());
            obraExistente.setTldr(obraAtualizada.getTldr());
            obraExistente.setPublicationDate(obraAtualizada.getPublicationDate());
            obraExistente.setYear(obraAtualizada.getYear());
            return obraRepository.save(obraExistente);
        }
        return null;
    }

    public boolean deletarObra(Long id) {
        if (obraRepository.existsById(id)) {
            obraRepository.deleteById(id);
            return true;
        }
        return false;
    }
};


    


