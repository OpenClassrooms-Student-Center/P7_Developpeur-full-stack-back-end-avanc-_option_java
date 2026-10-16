package com.openclassrooms.minilien.lien;

public class AccesRefuseException extends RuntimeException {

    public AccesRefuseException() {
        super("Jeton d'administration absent ou incorrect (en-tête X-Admin-Token).");
    }
}
