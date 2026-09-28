# DeepTime Atlas

DeepTime Atlas is a web application for exploring fossil occurrence records by taxon, geological interval, and modern discovery location.

The project is currently under development. Its backend is built with Java and Spring Boot and integrates data from the Paleobiology Database.

## Current Features

- Search for taxonomic names.
- Retrieve fossil occurrence records for a selected taxon.
- Return geographical coordinates and geological age estimates.
- Retrieve the bibliographic reference associated with an occurrence.
- Provide structured error responses when external data is unavailable.

## Data Source and Licensing

Fossil occurrence, taxonomic, geographical, and bibliographic metadata are retrieved from the [Paleobiology Database](https://paleobiodb.org/) through its public API:

- `taxa/auto` for taxon search;
- `occs/list` for fossil occurrence records;
- `refs/single` for bibliographic references.

Public PBDB collection records used by this project are identified by PBDB as [CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/). DeepTime Atlas still provides attribution to PBDB and preserves record identifiers and bibliographic references for traceability.

Bibliographic metadata does not grant rights to reproduce the publications themselves. DeepTime Atlas displays citations and source links, not article contents. Images are not currently imported or redistributed.

Only publicly accessible API data is used. The application does not attempt to access embargoed or private records.

## Scientific Interpretation

Map coordinates represent present-day fossil collection or discovery locations. They do not represent the paleogeographic position of continents at the time the organism lived.

Occurrence counts represent records available in PBDB. They should not be interpreted automatically as exact specimen counts, species richness, or biodiversity estimates.

## Backend

The backend currently exposes:

```text
GET /api/taxa/search?name=Tyrannosaurus
GET /api/taxa/Tyrannosaurus/occurrences
GET /api/references/4218
```
