package dev.laur.deeptimeatlas.taxon.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonSearchResponse;


@Component
public class PbdbClient {
    private final RestClient restClient;

    public PbdbClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://paleobiodb.org/data1.2")
                .build();
    }
    
    public PbdbTaxonSearchResponse searchTaxa(String name) {
    return restClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/taxa/auto.json")
                    .queryParam("name", name)
                    .queryParam("limit", 10)
                    .queryParam("vocab", "pbdb")
                    .build())
            .retrieve()
            .body(PbdbTaxonSearchResponse.class);
}
    
}
