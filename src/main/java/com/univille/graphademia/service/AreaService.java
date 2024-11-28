package com.univille.graphademia.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.univille.graphademia.node.Area;
import com.univille.graphademia.repository.AreaRepository;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;
    
    public Area salvarArea(Area area) {
        return areaRepository.save(area);
    }
    
};
