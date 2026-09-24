package dev.laur.deeptimeatlas.taxon.dto;

public record TaxonSearchResult(
    Long id,
    String name,
    String rank,
    Integer occurrenceCount
) {
    
}
    

