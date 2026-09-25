package dev.laur.deeptimeatlas.taxon.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PbdbReferenceResponse(
        @JsonProperty("elapsed_time") Double elapsedTime,
        List<PbdbReferenceRecord> records
) {
}