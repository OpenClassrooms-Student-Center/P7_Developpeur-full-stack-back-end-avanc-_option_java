package com.openclassrooms.minilien.lien;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/liens")
public class LienController {

    private final LienService service;
    private final String baseUrl;
    private final String adminToken;

    public LienController(LienService service,
                          @Value("${minilien.base-url}") String baseUrl,
                          @Value("${minilien.admin-token}") String adminToken) {
        this.service = service;
        this.baseUrl = baseUrl;
        this.adminToken = adminToken;
    }

    @PostMapping
    public ResponseEntity<LienDto> creer(@RequestBody(required = false) CreationLienRequete requete) {
        Lien lien = service.creer(requete == null ? null : requete.url());
        return ResponseEntity.created(URI.create("/api/liens/" + lien.getCode()))
                .body(LienDto.depuis(lien, baseUrl));
    }

    @GetMapping
    public List<LienDto> lister() {
        return service.lister().stream().map(lien -> LienDto.depuis(lien, baseUrl)).toList();
    }

    @GetMapping("/{code}")
    public LienDto consulter(@PathVariable String code) {
        return LienDto.depuis(service.trouver(code), baseUrl);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> supprimer(@PathVariable String code,
                                          @RequestHeader(value = "X-Admin-Token", required = false) String jeton) {
        if (jeton == null || !jeton.equals(adminToken)) {
            throw new AccesRefuseException();
        }
        service.supprimer(code);
        return ResponseEntity.noContent().build();
    }
}
