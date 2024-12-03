package com.univille.graphademia.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.univille.graphademia.node.Obra;

@RepositoryRestResource(collectionResourceRel = "obras", path = "obras")
public interface ObraRepository extends PagingAndSortingRepository<Obra, Long>, CrudRepository<Obra, Long> {
    Obra findById(String id);
};
