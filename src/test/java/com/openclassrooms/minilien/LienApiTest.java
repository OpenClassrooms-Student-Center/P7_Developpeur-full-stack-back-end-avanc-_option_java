package com.openclassrooms.minilien;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest(properties = "minilien.admin-token=jeton-de-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LienApiTest {

    @Autowired
    private MockMvc mvc;

    private String creerLien(String url) throws Exception {
        String reponse = mvc.perform(post("/api/liens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"" + url + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(reponse, "$.code");
    }

    @Test
    void creeUnLienCourtPourUneUrlValide() throws Exception {
        mvc.perform(post("/api/liens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"https://openclassrooms.com/fr/\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/liens/[A-Za-z0-9]{6}")))
                .andExpect(jsonPath("$.code").value(matchesPattern("[A-Za-z0-9]{6}")))
                .andExpect(jsonPath("$.url").value("https://openclassrooms.com/fr/"))
                .andExpect(jsonPath("$.lienCourt").value(matchesPattern("http://localhost:8080/r/[A-Za-z0-9]{6}")))
                .andExpect(jsonPath("$.visites").value(0));
    }

    @Test
    void refuseUneUrlInvalide() throws Exception {
        mvc.perform(post("/api/liens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"ftp://exemple.fr\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("URL_INVALIDE"));
    }

    @Test
    void redirigeEtCompteLesVisites() throws Exception {
        String code = creerLien("https://exemple.fr/page");

        mvc.perform(get("/r/" + code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://exemple.fr/page"));
        mvc.perform(get("/r/" + code));

        mvc.perform(get("/api/liens/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visites").value(2));
    }

    @Test
    void listeLesLiens() throws Exception {
        creerLien("https://exemple.fr/1");
        creerLien("https://exemple.fr/2");

        mvc.perform(get("/api/liens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void renvoie404PourUnCodeInconnu() throws Exception {
        mvc.perform(get("/r/XXXXXX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("LIEN_INTROUVABLE"));
    }

    @Test
    void refuseLaSuppressionSansJetonValide() throws Exception {
        String code = creerLien("https://exemple.fr");

        mvc.perform(delete("/api/liens/" + code))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/liens/" + code).header("X-Admin-Token", "mauvais"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void supprimeUnLienAvecLeBonJeton() throws Exception {
        String code = creerLien("https://exemple.fr");

        mvc.perform(delete("/api/liens/" + code).header("X-Admin-Token", "jeton-de-test"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/liens/" + code))
                .andExpect(status().isNotFound());
    }
}
