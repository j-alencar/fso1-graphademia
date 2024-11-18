package com.univille.graphademia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univille.graphademia.node.Autor;
import com.univille.graphademia.service.AutorService;


@RestController
@RequestMapping("/autores")
public class AutorController {

    @Autowired
    private AutorService autorService;

    @PostMapping
    public Autor criarAutor(@RequestBody Autor autor) {
        return autorService.salvarAutor(autor);
    }

//     @GetMapping
//     public List<Autor> getAllObras() {
//         return autorService.findAll();
//     }

}
