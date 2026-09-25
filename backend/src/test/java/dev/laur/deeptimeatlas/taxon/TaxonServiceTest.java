package dev.laur.deeptimeatlas.taxon;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.laur.deeptimeatlas.taxon.client.PbdbClient;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbOccurrenceListResponse;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbOccurrenceRecord;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbReferenceRecord;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbReferenceResponse;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonRecord;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonSearchResponse;
import dev.laur.deeptimeatlas.taxon.dto.BibliographicReferenceResult;
import dev.laur.deeptimeatlas.taxon.dto.FossilOccurrenceResult;
import dev.laur.deeptimeatlas.taxon.dto.TaxonSearchResult;
import dev.laur.deeptimeatlas.taxon.exception.ReferenceNotFoundException;

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
                87);

        PbdbTaxonSearchResponse response = new PbdbTaxonSearchResponse(0.001, List.of(pbdbRecord));

        when(pbdbClient.searchTaxa("Tyrannosaurus"))
                .thenReturn(response);

        List<TaxonSearchResult> results = taxonService.search("Tyrannosaurus");

        assertEquals(1, results.size());
        assertEquals(38613L, results.getFirst().id());
        assertEquals("Tyrannosaurus", results.getFirst().name());
        assertEquals("genus", results.getFirst().rank());
        assertEquals(87, results.getFirst().occurrenceCount());

        verify(pbdbClient).searchTaxa("Tyrannosaurus");

    }

    @Test
    void shouldMapPbdbOccurrencesToFossilOccurrenceResults() {
        PbdbClient pbdbClient = mock(PbdbClient.class);
        TaxonService taxonService = new TaxonService(pbdbClient);

        PbdbOccurrenceRecord pbdbRecord = new PbdbOccurrenceRecord(
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

        PbdbOccurrenceListResponse response = new PbdbOccurrenceListResponse(
                0.00732,
                List.of(pbdbRecord));

        when(pbdbClient.getOccurrences("Tyrannosaurus"))
                .thenReturn(response);

        List<FossilOccurrenceResult> results = taxonService.getOccurrences("Tyrannosaurus");

        assertEquals(1, results.size());
        assertEquals(139292L, results.getFirst().id());
        assertEquals("Tyrannosaurus rex", results.getFirst().acceptedName());
        assertEquals("Late Maastrichtian", results.getFirst().earlyInterval());
        assertEquals(72.2, results.getFirst().maxAgeMa());
        assertEquals(66.0, results.getFirst().minAgeMa());
        assertEquals(-113.028900, results.getFirst().longitude());
        assertEquals(51.906399, results.getFirst().latitude());
        assertEquals("Alberta", results.getFirst().region());

        verify(pbdbClient).getOccurrences("Tyrannosaurus");
    }

    @Test
    void shouldMapPbdbReferenceToBibliographicReferenceResult() {
            PbdbClient pbdbClient = mock(PbdbClient.class);
            TaxonService taxonService = new TaxonService(pbdbClient);

            PbdbReferenceRecord pbdbRecord = new PbdbReferenceRecord(
                            4218L,
                            "guidebook",
                            "Alberta's dinosaurs and other fossil vertebrates",
                            "2001",
                            "Museum of the Rockies Occasional Paper",
                            "D. A. Eberth et al. 2001. Alberta's dinosaurs.",
                            "English",
                            null);

            PbdbReferenceResponse response = new PbdbReferenceResponse(
                            0.000885,
                            List.of(pbdbRecord));

            when(pbdbClient.getReference(4218L))
                            .thenReturn(response);

            BibliographicReferenceResult result = taxonService.getReference(4218L);

            assertEquals(4218L, result.id());
            assertEquals(
                            "Alberta's dinosaurs and other fossil vertebrates",
                            result.title());
            assertEquals("2001", result.publicationYear());
            assertEquals("guidebook", result.publicationType());
            assertEquals("English", result.language());
            assertEquals(
                            "https://paleobiodb.org/classic/displayReference?reference_no=4218",
                            result.sourceUrl());

            verify(pbdbClient).getReference(4218L);
    }
    

    @Test
void shouldThrowExceptionWhenReferenceDoesNotExist() {
    PbdbClient pbdbClient = mock(PbdbClient.class);
    TaxonService taxonService = new TaxonService(pbdbClient);

    PbdbReferenceResponse emptyResponse =
            new PbdbReferenceResponse(
                    0.0001,
                    List.of()
            );

    when(pbdbClient.getReference(999999L))
            .thenReturn(emptyResponse);

    ReferenceNotFoundException exception = assertThrows(
            ReferenceNotFoundException.class,
            () -> taxonService.getReference(999999L)
    );

    assertEquals(
            "Bibliographic reference not found: 999999",
            exception.getMessage()
    );

    verify(pbdbClient).getReference(999999L);
}

}
