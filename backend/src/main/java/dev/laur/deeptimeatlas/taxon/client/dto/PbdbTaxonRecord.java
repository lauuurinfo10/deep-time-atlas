package dev.laur.deeptimeatlas.taxon.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public record PbdbTaxonRecord(
    @JsonProperty ("taxon_no") Long taxonId,
    @JsonProperty("record_type")String recordType,
    @JsonProperty("taxon_rank")String rank,
    @JsonProperty("taxon_name")String name,
    @JsonProperty ("n_occs")Integer occurrenceCount
) {
    
}
