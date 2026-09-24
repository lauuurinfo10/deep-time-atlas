package dev.laur.deeptimeatlas.taxon;


import dev.laur.deeptimeatlas.taxon.client.PbdbClient;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonRecord;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonSearchResponse;
import dev.laur.deeptimeatlas.taxon.dto.TaxonSearchResult;
import org.junit.jupiter.api.Test;

import java.beans.Transient;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class TaxonServiceTest {

    @Test
    void shouldMapPbdbResponseToSearchResult() {
        PbdbClient pbdbClient = mock(PbdbClient.class);
        TaxonService taxonService = new TaxonService(pbdbClient);

        PbdbTaxonRecord pbdbRecord = new PbdbTaxonRecord(
                38613L,
                "txn",
                "genus",
                "Tyrannosaurus",
                87
        );

        PbdbTaxonSearchResponse response =
                new PbdbTaxonSearchResponse(0.001, List.of(pbdbRecord));

        when(pbdbClient.searchTaxa("Tyrannosaurus"))
                .thenReturn(response);

        List<TaxonSearchResult> results =
                taxonService.search("Tyrannosaurus");

        assertEquals(1, results.size());
        assertEquals(38613L, results.getFirst().id());
        assertEquals("Tyrannosaurus", results.getFirst().name());
        assertEquals("genus", results.getFirst().rank());
        assertEquals(87, results.getFirst().occurrenceCount());

        verify(pbdbClient).searchTaxa("Tyrannosaurus");
    
    }

    
}
