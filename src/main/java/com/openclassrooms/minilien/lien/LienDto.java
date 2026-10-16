package com.openclassrooms.minilien.lien;

import java.time.Instant;

public record LienDto(String code, String url, String lienCourt, Instant creeLe, long visites) {

    public static LienDto depuis(Lien lien, String baseUrl) {
        return new LienDto(lien.getCode(), lien.getUrl(), baseUrl + "/r/" + lien.getCode(),
                lien.getCreeLe(), lien.getVisites());
    }
}
