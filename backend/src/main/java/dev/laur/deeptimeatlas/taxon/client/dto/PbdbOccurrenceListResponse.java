package dev.laur.deeptimeatlas.taxon.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PbdbOccurrenceListResponse(
        @JsonProperty("elapsed_time") Double elapsedTime,
        List<PbdbOccurrenceRecord> records
) {
}