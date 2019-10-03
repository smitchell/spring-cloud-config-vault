package com.example.service.geography.controller;

import com.example.service.geography.domain.UrbanArea;
import com.example.service.geography.repository.UrbanAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrbanAreaController {

    private UrbanAreaRepository urbanAreaRepository;

    @Autowired
    public UrbanAreaController(UrbanAreaRepository urbanAreaRepository) {
        this.urbanAreaRepository = urbanAreaRepository;
    }

    @GetMapping("/listUrbanAreas")
    public Page<UrbanArea> listUrbanAreas(@RequestParam int page, @RequestParam int size) {
        return urbanAreaRepository.findAll(PageRequest.of(page, size, Sort.by("name").ascending()));
    }

    @GetMapping("/searchUrbanAreasByName")
    public Page<UrbanArea> searchUrbanAreasByName(@RequestParam String searchTerm, @RequestParam int page, @RequestParam int size) {
        final String percent = "%";
        return urbanAreaRepository.findByNameLikeOrderByNameAsc(percent.concat(searchTerm).concat(percent), PageRequest.of(page, size, Sort.by("name").ascending()));
    }
}
