package dev.laur.deeptimeatlas.taxon.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.laur.deeptimeatlas.taxon.TaxonService;
import dev.laur.deeptimeatlas.taxon.dto.BibliographicReferenceResult;
import dev.laur.deeptimeatlas.taxon.exception.ReferenceNotFoundException;

@WebMvcTest(ReferenceController.class)
class ReferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxonService taxonService;

    @Test
    void shouldReturnBibliographicReference() throws Exception {
        BibliographicReferenceResult result =
                new BibliographicReferenceResult(
                        4218L,
                        "Alberta's dinosaurs and other fossil vertebrates",
                        "2001",
                        "guidebook",
                        "Museum of the Rockies Occasional Paper",
                        "D. A. Eberth et al. 2001. Alberta's dinosaurs.",
                        "English",
                        null,
                        "https://paleobiodb.org/classic/displayReference?reference_no=4218"
                );

        when(taxonService.getReference(4218L))
                .thenReturn(result);

        mockMvc.perform(
                        get("/api/references/{referenceId}", 4218L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4218))
                .andExpect(jsonPath("$.title")
                        .value(
                                "Alberta's dinosaurs and other fossil vertebrates"
                        ))
                .andExpect(jsonPath("$.publicationYear").value("2001"))
                .andExpect(jsonPath("$.publicationType").value("guidebook"))
                .andExpect(jsonPath("$.language").value("English"))
                .andExpect(jsonPath("$.doi").doesNotExist())
                .andExpect(jsonPath("$.sourceUrl")
                        .value(
                                "https://paleobiodb.org/classic/displayReference?reference_no=4218"
                        ));
    }

    @Test
    void shouldReturnNotFoundWhenReferenceDoesNotExist()
            throws Exception {
        when(taxonService.getReference(999999L))
                .thenThrow(
                        new ReferenceNotFoundException(999999L)
                );

        mockMvc.perform(
                        get("/api/references/{referenceId}", 999999L)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Bibliographic reference not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Bibliographic reference not found: 999999"
                        ));
    }
}