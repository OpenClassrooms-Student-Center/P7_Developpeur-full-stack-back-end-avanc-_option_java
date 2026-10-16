package com.openclassrooms.minilien.lien;

public class LienIntrouvableException extends RuntimeException {

    public LienIntrouvableException(String code) {
        super("Aucun lien ne correspond au code « " + code + " ».");
    }
}
