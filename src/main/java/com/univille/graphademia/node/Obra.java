package com.univille.graphademia.node;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;


import org.springframework.data.neo4j.core.schema.GeneratedValue;

@Node
public class Obra {

  @Id @GeneratedValue private Long id;

  private String tituloObra;
  private String nomeAutor;
  private String ano;          
  private String doi;         // para artigos, opcional
  private String isbn;        // para livros, opcional
  private String url;         // url, opcional

    public String getTituloObra() {
        return tituloObra;
    }
    public void setTituloObra(String tituloObra) {
        this.tituloObra = tituloObra;
    }
    public String getNomeAutor() {
        return nomeAutor;
    }
    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }
    public String getAno() {
        return ano;
    }
    public void setAno(String ano) {
        this.ano = ano;
    }
    // public String getDoi() {
    //     return doi;
    // }
    // public void setDoi(String doi) {
    //     this.doi = doi;
    // }
    // public String getIsbn() {
    //     return isbn;
    // }
    // public void setIsbn(String isbn) {
    //     this.isbn = isbn;
    // }
    // public String getUrl() {
    //     return url;
    // }
    // public void setUrl(String url) {
    //     this.url = url;
    // }
  
}