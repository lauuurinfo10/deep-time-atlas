package dev.laur.deeptimeatlas.taxon.client;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import dev.laur.deeptimeatlas.taxon.client.dto.PbdbOccurrenceListResponse;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbReferenceResponse;
import dev.laur.deeptimeatlas.taxon.client.dto.PbdbTaxonSearchResponse;
import dev.laur.deeptimeatlas.taxon.client.exception.PbdbServiceUnavailableException;

@Component
public class PbdbClient {

    private final RestClient restClient;

    public PbdbClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://paleobiodb.org/data1.2")
                .build();
    }

    public PbdbTaxonSearchResponse searchTaxa(String name) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                    .path("/taxa/auto.json")
                    .queryParam("name", name)
                    .queryParam("limit", 10)
                    .queryParam("vocab", "pbdb")
                    .build())
                    .retrieve()
                    .body(PbdbTaxonSearchResponse.class);
        } catch (ResourceAccessException | HttpServerErrorException exception) {
            throw new PbdbServiceUnavailableException(
                    "Paleobiology Database is temporarily unavailable",
                    exception);
        }

    }

    public PbdbOccurrenceListResponse getOccurrences(String taxonName) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                    .path("/occs/list.json")
                    .queryParam("base_name", taxonName)
                    .queryParam("show", "coords,loc")
                    .queryParam("limit", 100)
                    .queryParam("vocab", "pbdb")
                    .build())
                    .retrieve()
                    .body(PbdbOccurrenceListResponse.class);
        } catch (ResourceAccessException
                | HttpServerErrorException exception) {
            throw new PbdbServiceUnavailableException(
                    "Paleobiology Database is temporarily unavailable",
                    exception);
        }
    }

    public PbdbReferenceResponse getReference(Long referenceId) {
    try {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/refs/single.json")
                        .queryParam("id", referenceId)
                        .queryParam("show", "both")
                        .queryParam("vocab", "pbdb")
                        .build())
                .retrieve()
                .body(PbdbReferenceResponse.class);
    } catch (HttpClientErrorException.NotFound exception) {
        return new PbdbReferenceResponse(
                null,
                List.of()
        );
    } catch (
            ResourceAccessException |
            HttpServerErrorException exception
    ) {
        throw new PbdbServiceUnavailableException(
                "Paleobiology Database is temporarily unavailable",
                exception
        );
    }
}
}
