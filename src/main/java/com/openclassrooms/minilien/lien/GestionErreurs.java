package com.openclassrooms.minilien.lien;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestionErreurs {

    @ExceptionHandler(UrlInvalideException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErreurApi urlInvalide(UrlInvalideException e) {
        return new ErreurApi("URL_INVALIDE", e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErreurApi corpsIllisible() {
        return new ErreurApi("REQUETE_INVALIDE", "Le corps de la requête doit être un JSON de la forme {\"url\": \"...\"}.");
    }

    @ExceptionHandler(LienIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErreurApi introuvable(LienIntrouvableException e) {
        return new ErreurApi("LIEN_INTROUVABLE", e.getMessage());
    }

    @ExceptionHandler(AccesRefuseException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErreurApi accesRefuse(AccesRefuseException e) {
        return new ErreurApi("ACCES_REFUSE", e.getMessage());
    }
}
