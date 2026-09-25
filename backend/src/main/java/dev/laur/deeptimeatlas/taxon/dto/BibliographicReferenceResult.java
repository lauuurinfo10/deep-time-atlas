package dev.laur.deeptimeatlas.taxon.dto;

public record BibliographicReferenceResult(
        Long id,
        String title,
        String publicationYear,
        String publicationType,
        String publicationTitle,
        String formattedCitation,
        String language,
        String doi,
        String sourceUrl
) {
}