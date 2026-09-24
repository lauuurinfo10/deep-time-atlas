package dev.laur.deeptimeatlas.taxon.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.laur.deeptimeatlas.taxon.TaxonService;
import dev.laur.deeptimeatlas.taxon.dto.TaxonSearchResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import dev.laur.deeptimeatlas.taxon.dto.FossilOccurrenceResult;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/taxa")
public class TaxonController {

    private final TaxonService taxonService;

    public TaxonController(TaxonService taxonService) {
        this.taxonService = taxonService;
    }

    @GetMapping("/search")
    public List<TaxonSearchResult> search(
            @RequestParam("name") @NotBlank(message = "Taxon name must not be blank") @Size(min = 3, max = 100, message = "Taxon name must contain between 3 and 100 characters") String name) {
        return taxonService.search(name);
    }

    @GetMapping("/{name}/occurrences")
    public List<FossilOccurrenceResult> getOccurrences(
            @PathVariable("name") @NotBlank(message = "Taxon name must not be blank") @Size(min = 3, max = 100, message = "Taxon name must contain between 3 and 100 characters") String name) {
        return taxonService.getOccurrences(name);
    }

}
