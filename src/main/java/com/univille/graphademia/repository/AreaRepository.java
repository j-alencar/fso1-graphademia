package com.univille.graphademia.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.univille.graphademia.node.Area;

@RepositoryRestResource(collectionResourceRel = "areas", path = "areas")
public interface AreaRepository extends PagingAndSortingRepository<Area, Long>, CrudRepository<Area, Long> {

};