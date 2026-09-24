package dev.laur.deeptimeatlas.taxon.dto;

public record FossilOccurrenceResult(
        Long id,
        Long collectionId,
        String identifiedName,
        String acceptedName,
        String rank,
        String earlyInterval,
        String lateInterval,
        Double maxAgeMa,
        Double minAgeMa,
        Long referenceId,
        Double longitude,
        Double latitude,
        String countryCode,
        String region,
        String coordinateBasis,
        String coordinatePrecision
) {
}