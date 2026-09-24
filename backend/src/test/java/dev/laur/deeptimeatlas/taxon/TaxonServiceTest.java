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

import dev.laur.deeptimeatlas.taxon.client.dto.PbdbOccurrenceListResponse;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbOccurrenceRecord;
import dev.laur.deeptimeatlas.taxon.dto.FossilOccurrenceResult;

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

}
