package com.univille.graphademia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univille.graphademia.node.Area;
import com.univille.graphademia.service.AreaService;


@RestController
@RequestMapping("/areas")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @PostMapping
    public Area criarArea(@RequestBody Area area) {
        return areaService.salvarArea(area);
    }

};
