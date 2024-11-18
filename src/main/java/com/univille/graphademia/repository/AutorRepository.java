package com.univille.graphademia.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.univille.graphademia.node.Autor;

@RepositoryRestResource(collectionResourceRel = "autores", path = "autores")
public interface AutorRepository extends PagingAndSortingRepository<Autor, Long>, CrudRepository<Autor, Long> {

  // List<Autor> pesquisarAutorPorNome(@Param("name") String name);

}