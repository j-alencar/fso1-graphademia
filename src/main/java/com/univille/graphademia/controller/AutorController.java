package com.univille.graphademia.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.univille.graphademia.node.Autor;
import com.univille.graphademia.service.AutorService;
import com.univille.graphademia.service.SemanticScholarAPIService;


@Controller
@RequestMapping("/autores")
public class AutorController {

    private static final Logger logger = LoggerFactory.getLogger(AutorController.class);

    @Autowired
    private AutorService autorService;

    @GetMapping("/visualizar")
    public String visualizarAutores(Model model) {
        model.addAttribute("autores", autorService.buscarTodosAutores());
        return "autores";
    }

    @PostMapping
    public Autor criarAutor(@RequestBody Autor autor) {
        return autorService.salvarAutor(autor);
    }

    @GetMapping("/edit/{id}")
    public String editarAutorForm(@PathVariable Long id, Model model) {
        Autor autor = autorService.buscarAutorPorId(id);
        if (autor == null) {
            return "redirect:/autores/visualizar";
        }
        model.addAttribute("autor", autor);
        return "editar-autor";
    }

    @PostMapping("/edit/{id}")
    public String atualizarAutor(@PathVariable Long id, @ModelAttribute Autor autorAtualizado) {
        autorService.atualizarAutor(id, autorAtualizado);
        return "redirect:/autores/visualizar"; // Redirecionar dps de editar
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deletarAutor(@PathVariable Long id) {
        boolean deletado = autorService.deletarAutor(id);
        if (deletado) {
            return ResponseEntity.ok("Autor deletado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Autor não encontrado!");
        }
    }

    @PostMapping("/edit/{id}/autopopular")
    public String autoPopularCamposAutor(@PathVariable Long id, Model model) {
        Autor autor = autorService.buscarAutorPorId(id);
        if (autor == null) {
            return "redirect:/autores/visualizar";
        }
        try {
            List<Autor> detalhesPopulados = SemanticScholarAPIService.gerarDetalhesMultiplosAutores(List.of(autor));
            if (detalhesPopulados != null && !detalhesPopulados.isEmpty()) {
                Autor populado = detalhesPopulados.get(0);
                autor.setName(populado.getName());
                autor.setHindex(populado.getHindex());
                autor.setDblp(populado.getDblp());
                autor.setOrcid(populado.getOrcid());
                autorService.atualizarAutor(id, autor);
            } else {
                logger.warn("Nenhum detalhe encontrado para o autor de ID {}", id);
            }
        } catch (Exception e) {
            logger.error("Erro durante autopopulação de campos do autor de ID {}: {}", id, e.getMessage(), e);
        }

        return "redirect:/autores/edit/" + id;
    }
    
};
