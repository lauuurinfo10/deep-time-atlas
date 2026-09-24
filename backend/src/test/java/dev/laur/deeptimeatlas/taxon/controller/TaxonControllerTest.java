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

import dev.laur.deeptimeatlas.taxon.dto.FossilOccurrenceResult;
import static org.mockito.Mockito.verify;

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
                                                .param("name", "Ty"))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(taxonService);
        }

        @Test
        void shouldReturnOccurrencesForValidTaxonName() throws Exception {
                FossilOccurrenceResult occurrence = new FossilOccurrenceResult(
                                139292L,
                                11917L,
                                "Tyrannosaurus rex",
                                "Tyrannosaurus rex",
                                "species",
                                "Late Maastrichtian",
                                null,
                                72.2,
                                66.0,
                                4218L,
                                -113.028900,
                                51.906399,
                                "CA",
                                "Alberta",
                                "stated in text",
                                "4");

                when(taxonService.getOccurrences("Tyrannosaurus"))
                                .thenReturn(List.of(occurrence));

                mockMvc.perform(
                                get(
                                                "/api/taxa/{name}/occurrences",
                                                "Tyrannosaurus"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(139292))
                                .andExpect(jsonPath("$[0].acceptedName")
                                                .value("Tyrannosaurus rex"))
                                .andExpect(jsonPath("$[0].earlyInterval")
                                                .value("Late Maastrichtian"))
                                .andExpect(jsonPath("$[0].maxAgeMa").value(72.2))
                                .andExpect(jsonPath("$[0].minAgeMa").value(66.0))
                                .andExpect(jsonPath("$[0].longitude")
                                                .value(-113.028900))
                                .andExpect(jsonPath("$[0].latitude")
                                                .value(51.906399))
                                .andExpect(jsonPath("$[0].region").value("Alberta"));

                verify(taxonService).getOccurrences("Tyrannosaurus");
        }
}
