import { useRef, useState, type FormEvent } from "react";
import {
  ArrowDown,
  Database,
  Filter,
  MapPin,
  Search,
  X,
} from "lucide-react";
import {
  getOccurrences,
  OCCURRENCE_PAGE_SIZE,
} from "./api/occurrence";
import { searchTaxa } from "./api/taxa";
import OccurrenceMap from "./components/OccurrenceMap";
import type { FossilOccurrence } from "./types/occurrence";
import type { TaxonSearchResult } from "./types/taxon";
import "./App.css";

function formatAge(occurrence: FossilOccurrence) {
  if (
    occurrence.maxAgeMa !== null &&
    occurrence.minAgeMa !== null
  ) {
    return `${occurrence.maxAgeMa} - ${occurrence.minAgeMa} Ma`;
  }

  return occurrence.earlyInterval ?? "Age unavailable";
}

function formatLocation(occurrence: FossilOccurrence) {
  return [occurrence.region, occurrence.countryCode]
    .filter(Boolean)
    .join(", ") || "Location unavailable";
}

function App() {
  const [taxonName, setTaxonName] = useState("");
  const [searchResults, setSearchResults] = useState<TaxonSearchResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [selectedTaxon, setSelectedTaxon] = useState<TaxonSearchResult | null>(null);
  const [occurrences, setOccurrences] = useState<FossilOccurrence[]>([]);
  const [isOccurrenceLoading, setIsOccurrenceLoading] = useState(false);
  const [occurrenceError, setOccurrenceError] = useState<string | null>(null);
  const [hasMoreOccurrences, setHasMoreOccurrences] = useState(false);
  const [intervalDraft, setIntervalDraft] = useState("");
  const [activeInterval, setActiveInterval] = useState("");
  const occurrenceRequestId = useRef(0);

  async function loadOccurrences(
    taxon: TaxonSearchResult,
    interval: string,
    offset: number,
    append: boolean,
  ) {
    const requestId = occurrenceRequestId.current + 1;
    occurrenceRequestId.current = requestId;

    setIsOccurrenceLoading(true);
    setOccurrenceError(null);

    try {
      const results = await getOccurrences({
        taxonName: taxon.name,
        interval,
        limit: OCCURRENCE_PAGE_SIZE + 1,
        offset,
      });

      if (occurrenceRequestId.current !== requestId) {
        return;
      }

      const page = results.slice(0, OCCURRENCE_PAGE_SIZE);

      setOccurrences((current) => (append ? [...current, ...page] : page));
      setHasMoreOccurrences(results.length > OCCURRENCE_PAGE_SIZE);
    } catch {
      if (occurrenceRequestId.current === requestId) {
        setOccurrenceError("The fossil occurrences could not be loaded.");
      }
    } finally {
      if (occurrenceRequestId.current === requestId) {
        setIsOccurrenceLoading(false);
      }
    }
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const normalizedName = taxonName.trim();

    if (!normalizedName) {
      return;
    }

    occurrenceRequestId.current += 1;
    setIsLoading(true);
    setHasSearched(true);
    setError(null);
    setSearchResults([]);
    setSelectedTaxon(null);
    setOccurrences([]);
    setOccurrenceError(null);
    setHasMoreOccurrences(false);
    setIntervalDraft("");
    setActiveInterval("");

    try {
      const results = await searchTaxa(normalizedName);
      setSearchResults(results);
    } catch {
      setError("The taxon search could not be completed.");
    } finally {
      setIsLoading(false);
      document.getElementById("explorer")?.scrollIntoView({ behavior: "smooth" });
    }
  }

  function handleTaxonSelect(taxon: TaxonSearchResult) {
    setSelectedTaxon(taxon);
    setOccurrences([]);
    setHasMoreOccurrences(false);
    setIntervalDraft("");
    setActiveInterval("");

    void loadOccurrences(taxon, "", 0, false);
  }

  function handleIntervalSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!selectedTaxon) {
      return;
    }

    const normalizedInterval = intervalDraft.trim();

    if (normalizedInterval.length === 1) {
      setOccurrenceError("A geological interval must contain at least 2 characters.");
      return;
    }

    setActiveInterval(normalizedInterval);
    setOccurrences([]);
    setHasMoreOccurrences(false);
    void loadOccurrences(selectedTaxon, normalizedInterval, 0, false);
  }

  function clearInterval() {
    if (!selectedTaxon) {
      return;
    }

    setIntervalDraft("");
    setActiveInterval("");
    setOccurrences([]);
    setHasMoreOccurrences(false);
    void loadOccurrences(selectedTaxon, "", 0, false);
  }

  const mappedOccurrenceCount = occurrences.filter(
    (occurrence) => occurrence.longitude !== null && occurrence.latitude !== null,
  ).length;

  return (
    <main className="app">
      <section className="hero">
        <header className="site-header">
          <a className="brand" href="/">DeepTime Atlas</a>

          <a
            className="data-source"
            href="https://paleobiodb.org/"
            target="_blank"
            rel="noreferrer"
          >
            <Database size={17} aria-hidden="true" />
            PBDB data
          </a>
        </header>

        <div className="hero-content">
          <p className="eyebrow">Fossil occurrence explorer</p>
          <h1>DeepTime Atlas</h1>

          <p className="hero-description">
            Explore fossil records across geological time and modern discovery locations.
          </p>

          <form className="taxon-search" onSubmit={handleSubmit}>
            <label className="sr-only" htmlFor="taxon-name">Taxon name</label>
            <Search size={21} aria-hidden="true" />
            <input
              id="taxon-name"
              type="search"
              placeholder="Tyrannosaurus, Trilobita, Canis..."
              value={taxonName}
              onChange={(event) => setTaxonName(event.target.value)}
            />
            <button type="submit" disabled={!taxonName.trim() || isLoading}>
              {isLoading ? "Searching..." : "Explore"}
            </button>
          </form>
        </div>

        <a className="scroll-link" href="#explorer" aria-label="Go to occurrence explorer">
          <ArrowDown size={22} aria-hidden="true" />
        </a>
      </section>

      <section className="explorer" id="explorer">
        <div className="explorer-header">
          <div>
            <p className="eyebrow">Occurrence explorer</p>
            <h2>Fossil records, mapped</h2>
          </div>

          {hasSearched && !isLoading && !error && (
            <p className="result-count">{searchResults.length} matches</p>
          )}
        </div>

        {isLoading && <p className="search-status">Searching PBDB...</p>}

        {error && <p className="search-error" role="alert">{error}</p>}

        {hasSearched && !isLoading && !error && searchResults.length === 0 && (
          <p className="search-status">No taxa found.</p>
        )}

        {!isLoading && searchResults.length > 0 && (
          <ol className="taxon-results">
            {searchResults.map((result) => (
              <li key={result.id}>
                <button
                  className="taxon-result-button"
                  type="button"
                  aria-pressed={selectedTaxon?.id === result.id}
                  onClick={() => handleTaxonSelect(result)}
                >
                  <div>
                    <strong>{result.name}</strong>
                    <span>{result.rank ?? "Unranked"}</span>
                  </div>
                  <span>{result.occurrenceCount ?? "Unknown"} PBDB records</span>
                </button>
              </li>
            ))}
          </ol>
        )}

        {selectedTaxon && (
          <section className="occurrence-summary" aria-live="polite">
            <div>
              <p className="eyebrow">Selected taxon</p>
              <h3>{selectedTaxon.name}</h3>
            </div>

            <form className="interval-filter" onSubmit={handleIntervalSubmit}>
              <label htmlFor="geological-interval">Geological interval</label>
              <div>
                <Filter size={16} aria-hidden="true" />
                <input
                  id="geological-interval"
                  type="search"
                  minLength={2}
                  placeholder="Maastrichtian"
                  value={intervalDraft}
                  onChange={(event) => setIntervalDraft(event.target.value)}
                />
                <button type="submit" disabled={isOccurrenceLoading}>Apply</button>
                {activeInterval && (
                  <button
                    className="clear-filter"
                    type="button"
                    onClick={clearInterval}
                    aria-label="Clear geological interval filter"
                  >
                    <X size={17} aria-hidden="true" />
                  </button>
                )}
              </div>
            </form>

            <div className="occurrence-status">
              {isOccurrenceLoading && <p>Loading records...</p>}
              {occurrenceError && <p className="search-error" role="alert">{occurrenceError}</p>}
              {!isOccurrenceLoading && !occurrenceError && (
                <p>{occurrences.length} currently loaded, {mappedOccurrenceCount} available for the map.</p>
              )}
            </div>
          </section>
        )}

        {selectedTaxon && !isOccurrenceLoading && !occurrenceError && occurrences.length === 0 && (
          <p className="empty-occurrences">
            No occurrences matched this search{activeInterval ? ` for ${activeInterval}` : ""}.
          </p>
        )}

        {selectedTaxon && !occurrenceError && mappedOccurrenceCount > 0 && (
          <section className="map-section">
            <div className="map-section-header">
              <div>
                <p className="eyebrow">Discovery locations</p>
                <h3>Modern collection coordinates</h3>
              </div>
              <p>{mappedOccurrenceCount} mapped records</p>
            </div>

            <OccurrenceMap
              key={`${selectedTaxon.id}-${activeInterval}`}
              occurrences={occurrences}
            />

            <p className="map-note">
              Coordinates represent modern fossil collection locations, not the position of continents when the organisms lived. Cluster counts represent PBDB records, not individual specimens.
            </p>
          </section>
        )}

        {selectedTaxon && !occurrenceError && occurrences.length > 0 && (
          <section className="occurrence-list-section">
            <div className="occurrence-list-header">
              <div>
                <p className="eyebrow">Loaded records</p>
                <h3>Occurrence details</h3>
              </div>
              <p>{occurrences.length} records</p>
            </div>

            <ol className="occurrence-list">
              {occurrences.map((occurrence) => (
                <li key={occurrence.id}>
                  <article>
                    <div>
                      <h4>
                        {occurrence.acceptedName ?? occurrence.identifiedName ?? `Occurrence ${occurrence.id}`}
                      </h4>
                      <p>{formatAge(occurrence)}</p>
                    </div>
                    <div className="occurrence-meta">
                      <span>
                        <MapPin size={15} aria-hidden="true" />
                        {formatLocation(occurrence)}
                      </span>
                      <span>
                        {occurrence.longitude !== null && occurrence.latitude !== null
                          ? "Mapped"
                          : "Coordinates unavailable"}
                      </span>
                    </div>
                  </article>
                </li>
              ))}
            </ol>

            {hasMoreOccurrences && (
              <button
                className="load-more"
                type="button"
                disabled={isOccurrenceLoading}
                onClick={() => {
                  void loadOccurrences(
                    selectedTaxon,
                    activeInterval,
                    occurrences.length,
                    true,
                  );
                }}
              >
                {isOccurrenceLoading ? "Loading..." : "Load more records"}
              </button>
            )}
          </section>
        )}
      </section>
    </main>
  );
}

export default App;
