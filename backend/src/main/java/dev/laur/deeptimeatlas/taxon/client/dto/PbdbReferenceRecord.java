package dev.laur.deeptimeatlas.taxon.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PbdbReferenceRecord(
        @JsonProperty("reference_no") Long referenceId,
        @JsonProperty("publication_type") String publicationType,
        @JsonProperty("reftitle") String title,
        @JsonProperty("pubyr") String publicationYear,
        @JsonProperty("pubtitle") String publicationTitle,
        @JsonProperty("formatted") String formattedCitation,
        String language,
        String doi
) {
}