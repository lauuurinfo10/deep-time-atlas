package dev.laur.deeptimeatlas.taxon;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.laur.deeptimeatlas.taxon.client.PbdbClient;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonRecord;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonSearchResponse;
import dev.laur.deeptimeatlas.taxon.dto.TaxonSearchResult;

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
                    pbdbRecord.occurrenceCount()
            );

            results.add(result);
        }

        return results;
    }
}
