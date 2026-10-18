package com.openclassrooms.minilien.lien;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectionController {

    private final LienService service;

    public RedirectionController(LienService service) {
        this.service = service;
    }

    @GetMapping("/r/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        Lien lien = service.registerVisit(code);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(lien.getUrl())).build();
    }
}
