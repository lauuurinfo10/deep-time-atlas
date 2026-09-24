package dev.laur.deeptimeatlas.taxon.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.laur.deeptimeatlas.taxon.TaxonService;
import dev.laur.deeptimeatlas.taxon.dto.TaxonSearchResult;


@WebMvcTest(TaxonController.class)
class TaxonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxonService taxonService;

    @Test
    void shouldReturnTaxaForValidName() throws Exception {
            TaxonSearchResult result = new TaxonSearchResult(38613L, "Tyrannosaurus", "genus", 87);

            when(taxonService.search("Tyrannosaurus"))
                            .thenReturn(List.of(result));

            mockMvc.perform(
                            get("/api/taxa/search")
                                            .param("name", "Tyrannosaurus"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$[0].id").value(38613))
                            .andExpect(jsonPath("$[0].name").value("Tyrannosaurus"))
                            .andExpect(jsonPath("$[0].rank").value("genus"))
                            .andExpect(jsonPath("$[0].occurrenceCount").value(87));
    }
    
    @Test
void shouldReturnBadRequestForNameShorterThanThreeCharacters() throws Exception {
    mockMvc.perform(
                    get("/api/taxa/search")
                            .param("name", "Ty")
            )
            .andExpect(status().isBadRequest());

    verifyNoInteractions(taxonService);
}
}
