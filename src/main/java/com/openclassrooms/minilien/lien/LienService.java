package com.openclassrooms.minilien.lien;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/** Stockage en mémoire des liens courts (les données sont perdues au redémarrage). */
@Service
public class LienService {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LONGUEUR_CODE = 6;
    private static final int LONGUEUR_MAX_URL = 2048;

    private final Map<String, Lien> liens = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public Lien create(String url) {
        checkUrl(url);
        while (true) {
            Lien lien = new Lien(generateCode(), url.trim(), Instant.now());
            if (liens.putIfAbsent(lien.getCode(), lien) == null) {
                return lien;
            }
        }
    }

    public List<Lien> list() {
        return liens.values().stream()
                .sorted(Comparator.comparing(Lien::getCreeLe).reversed())
                .toList();
    }

    public Lien find(String code) {
        Lien lien = liens.get(code);
        if (lien == null) {
            throw new LienIntrouvableException(code);
        }
        return lien;
    }

    public Lien registerVisit(String code) {
        Lien lien = find(code);
        lien.recordVisit();
        return lien;
    }

    public void delete(String code) {
        if (liens.remove(code) == null) {
            throw new LienIntrouvableException(code);
        }
    }

    private void checkUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new UrlInvalideException("Le champ « url » est obligatoire.");
        }
        if (url.length() > LONGUEUR_MAX_URL) {
            throw new UrlInvalideException("L'URL dépasse " + LONGUEUR_MAX_URL + " caractères.");
        }
        try {
            URI uri = new URI(url.trim());
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equals("http") || scheme.equals("https")) || uri.getHost() == null) {
                throw new UrlInvalideException("L'URL doit commencer par http:// ou https:// et contenir un domaine.");
            }
        } catch (URISyntaxException e) {
            throw new UrlInvalideException("L'URL n'est pas valide.");
        }
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(LONGUEUR_CODE);
        for (int i = 0; i < LONGUEUR_CODE; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }
}
