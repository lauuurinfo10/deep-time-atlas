package dev.laur.deeptimeatlas.taxon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.laur.deeptimeatlas.taxon.TaxonService;
import dev.laur.deeptimeatlas.taxon.dto.BibliographicReferenceResult;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/references")
public class ReferenceController {

    private final TaxonService taxonService;

    public ReferenceController(TaxonService taxonService) {
        this.taxonService = taxonService;
    }

    @GetMapping("/{referenceId}")
    public BibliographicReferenceResult getReference(
            @PathVariable("referenceId")
            @Positive(message = "Reference ID must be positive")
            Long referenceId
    ) {
        return taxonService.getReference(referenceId);
    }
}