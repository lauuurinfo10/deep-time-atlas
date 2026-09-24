package dev.laur.deeptimeatlas.taxon.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PbdbOccurrenceRecord(
        @JsonProperty("occurrence_no") Long occurrenceId,
        @JsonProperty("collection_no") Long collectionId,
        @JsonProperty("identified_name") String identifiedName,
        @JsonProperty("accepted_name") String acceptedName,
        @JsonProperty("accepted_rank") String acceptedRank,
        @JsonProperty("early_interval") String earlyInterval,
        @JsonProperty("late_interval") String lateInterval,
        @JsonProperty("max_ma") Double maxAgeMa,
        @JsonProperty("min_ma") Double minAgeMa,
        @JsonProperty("reference_no") Long referenceId,
        @JsonProperty("lng") Double longitude,
        @JsonProperty("lat") Double latitude,
        @JsonProperty("cc") String countryCode,
        @JsonProperty("state") String state,
        @JsonProperty("latlng_basis") String coordinateBasis,
        @JsonProperty("latlng_precision") String coordinatePrecision
) {
}