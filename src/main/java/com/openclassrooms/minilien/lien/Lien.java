package com.openclassrooms.minilien.lien;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/** Un lien court : un code de 6 caractères qui redirige vers une URL cible. */
public class Lien {

    private final String code;
    private final String url;
    private final Instant creeLe;
    private final AtomicLong visites = new AtomicLong();

    public Lien(String code, String url, Instant creeLe) {
        this.code = code;
        this.url = url;
        this.creeLe = creeLe;
    }

    public String getCode() {
        return code;
    }

    public String getUrl() {
        return url;
    }

    public Instant getCreeLe() {
        return creeLe;
    }

    public long getVisites() {
        return visites.get();
    }

    public void enregistrerVisite() {
        visites.incrementAndGet();
    }
}
