package dev.laur.deeptimeatlas.taxon;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.laur.deeptimeatlas.taxon.client.PbdbClient;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonRecord;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonSearchResponse;
import dev.laur.deeptimeatlas.taxon.dto.TaxonSearchResult;

import dev.laur.deeptimeatlas.taxon.client.dto.PbdbOccurrenceListResponse;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbOccurrenceRecord;
import dev.laur.deeptimeatlas.taxon.dto.FossilOccurrenceResult;

@Service
public class TaxonService {

       private final PbdbClient pbdbClient;

    public TaxonService(PbdbClient pbdbClient) {
        this.pbdbClient = pbdbClient;
    }

    public List<TaxonSearchResult> search(String name) {
        PbdbTaxonSearchResponse response = pbdbClient.searchTaxa(name);
        List<TaxonSearchResult> results = new ArrayList<>();

        if (response == null || response.records() == null) {
            return results;
        }

        for (PbdbTaxonRecord pbdbRecord : response.records()) {
            TaxonSearchResult result = new TaxonSearchResult(
                    pbdbRecord.taxonId(),
                    pbdbRecord.name(),
                    pbdbRecord.rank(),
                    pbdbRecord.occurrenceCount());

            results.add(result);
        }

        return results;
    }

    
    public List<FossilOccurrenceResult> getOccurrences(String taxonName) {
        PbdbOccurrenceListResponse response = pbdbClient.getOccurrences(taxonName);

        List<FossilOccurrenceResult> results = new ArrayList<>();

        if (response == null || response.records() == null) {
            return results;
        }

        for (PbdbOccurrenceRecord pbdbRecord : response.records()) {
            FossilOccurrenceResult result = new FossilOccurrenceResult(
                    pbdbRecord.occurrenceId(),
                    pbdbRecord.collectionId(),
                    pbdbRecord.identifiedName(),
                    pbdbRecord.acceptedName(),
                    pbdbRecord.acceptedRank(),
                    pbdbRecord.earlyInterval(),
                    pbdbRecord.lateInterval(),
                    pbdbRecord.maxAgeMa(),
                    pbdbRecord.minAgeMa(),
                    pbdbRecord.referenceId(),
                    pbdbRecord.longitude(),
                    pbdbRecord.latitude(),
                    pbdbRecord.countryCode(),
                    pbdbRecord.state(),
                    pbdbRecord.coordinateBasis(),
                    pbdbRecord.coordinatePrecision());

            results.add(result);
        }

        return results;

    }
}
