package dev.laur.deeptimeatlas.taxon.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.laur.deeptimeatlas.taxon.TaxonService;
import dev.laur.deeptimeatlas.taxon.dto.FossilOccurrenceResult;
import dev.laur.deeptimeatlas.taxon.dto.TaxonSearchResult;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
        @PathVariable("name")
        @NotBlank(message = "Taxon name must not be blank")
        @Size(min = 3, max = 100, message = "Taxon name must contain between 3 and 100 characters")
        String name,

        @RequestParam(name = "interval", required = false)
        @Size(min = 2, max = 100, message = "Interval must contain between 2 and 100 characters")
        String interval,

        @RequestParam(name = "limit", defaultValue = "100")
        @Min(value = 1, message = "Limit must be at least 1")
        @Max(value = 100, message = "Limit must not exceed 100")
        int limit,

        @RequestParam(name = "offset", defaultValue = "0")
        @Min(value = 0, message = "Offset must not be negative")
        int offset) {

    return taxonService.getOccurrences(
            name,
            interval,
            limit,
            offset);
}
    

}
