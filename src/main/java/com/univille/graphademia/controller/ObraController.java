package com.univille.graphademia.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
};
