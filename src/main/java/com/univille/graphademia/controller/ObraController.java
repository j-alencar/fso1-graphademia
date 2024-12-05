package com.univille.graphademia.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.univille.graphademia.dto.ObraResponse;
import com.univille.graphademia.node.Obra;
import com.univille.graphademia.service.ObraService;
import com.univille.graphademia.service.SemanticScholarAPIService;

@Controller
@RequestMapping("/obras")
public class ObraController {

    @Autowired
    private ObraService obraService;

    @Autowired
    private SemanticScholarAPIService semanticScholarAPIService;

    @GetMapping("/visualizar")
    public String visualizarObras(Model model) {
        model.addAttribute("obras", obraService.buscarTodasObras());
        return "obras"; 
    }

    @PostMapping
    public Obra criarObra(@RequestBody Obra obra) {
        return obraService.salvarObra(obra);
    }
    
    @GetMapping("/{id}/editar")
    public String editarObra(@PathVariable Long id, Model model) {
        Obra obra = obraService.buscarPorId(id);
        model.addAttribute("obra", obra);
        return "editar-obra"; 
    }

    @PostMapping("/{id}/editar")
    public String atualizarObra(@PathVariable Long id, @ModelAttribute Obra obraAtualizada) {
        obraService.atualizarObra(id, obraAtualizada);
        return "redirect:/obras/visualizar"; // Redirecionar dps de editar
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deletarObra(@PathVariable Long id) {
        boolean deletado = obraService.deletarObra(id);
        if (deletado) {
            return ResponseEntity.ok("Obra deletada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Obra não encontrada!");
        }
    }

    @GetMapping("/pesquisar-obra")
    public String exibirPagPesquisa() {
        return "pesquisar-obra";
    }

    @GetMapping("/pesquisar-obra/resultados")
    @ResponseBody
    public ResponseEntity<?> pesquisarObras(@RequestParam String titulo) {
        List<Obra> resultados = semanticScholarAPIService.gerarObraPorTitulo(titulo);
        if (resultados != null && !resultados.isEmpty()) {
            return ResponseEntity.ok(resultados); // Retorna lista de objs Obra
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum resultado encontrado para o título especificado.");
    }
    

    @PostMapping("/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarObraPesquisada(@RequestBody Obra obra) {
        if (obra == null || obra.getPaperId() == null || obra.getTitle() == null) {
            return ResponseEntity.badRequest().body("Dados inválidos: 'paperId' e 'title' são obrigatórios! Se não, a pesquisa não faz sentido!!!");
        }
        
        try {
            // Pesquisa mostra apenas alguns atributos, mas botão salvar precisa salva o obj inteiro
            Obra salva = obraService.salvarObra(obra);
            
            return ResponseEntity.ok().body("{\"status\": \"ok\"}"); // Msg de retorno
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/{id}/autopopular")
    @ResponseBody
    public ResponseEntity<?> autoPopularObra(@PathVariable Long id) {
        try {
            Obra obra = obraService.buscarPorId(id);
            
            if (obra == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ObraResponse("error", "Obra not found"));
            }
            
            obra = semanticScholarAPIService.atualizarObraPorId(obra, obra.getPaperId());
            
            return ResponseEntity.ok().body(new ObraResponse("ok", obra));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ObraResponse("error", "Erro: " + e.getMessage()));
        }
    }

};