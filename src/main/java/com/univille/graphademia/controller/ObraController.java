package com.univille.graphademia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univille.graphademia.node.Obra;
import com.univille.graphademia.service.ObraService;


@RestController
@RequestMapping("/obras")
public class ObraController {

    @Autowired
    private ObraService obraService;

    @PostMapping
    public Obra criarObra(@RequestBody Obra obra) {
        return obraService.salvarObra(obra);
    }

    @GetMapping
    public List<Obra> getAllObras() {
        return obraService.findAll();
    }

    @GetMapping("/autor/{nomeAutor}")
    public List<Obra> getObrasByAutor(@PathVariable String nomeAutor) {
        return obraService.findByNomeAutor(nomeAutor);
    }

}
