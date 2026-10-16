package com.openclassrooms.minilien.lien;

/** Format commun des erreurs renvoyées par l'API. */
public record ErreurApi(String code, String message) {
}
