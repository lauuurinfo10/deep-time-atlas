import { useState, type FormEvent } from "react";
import { ArrowDown, Database, Search } from "lucide-react";
import { searchTaxa } from "./api/taxa";
import type { TaxonSearchResult } from "./types/taxon";
import "./App.css";

function App() {
  const [taxonName, setTaxonName] = useState("");
  const [searchResults, setSearchResults] = useState<TaxonSearchResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const normalizedName = taxonName.trim();

    if (!normalizedName) {
      return;
    }

    setIsLoading(true);
    setHasSearched(true);
    setError(null);

    try {
      const results = await searchTaxa(normalizedName);
      setSearchResults(results);
    } catch {
      setSearchResults([]);
      setError("The taxon search could not be completed.");
    } finally {
      setIsLoading(false);

      document
        .getElementById("explorer")
        ?.scrollIntoView({ behavior: "smooth" });
    }
  }

  return (
    <main className="app">
      <section className="hero">
        <header className="site-header">
          <a className="brand" href="/">
            DeepTime Atlas
          </a>

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
            Explore fossil records across geological time and modern discovery
            locations.
          </p>

          <form className="taxon-search" onSubmit={handleSubmit}>
            <label className="sr-only" htmlFor="taxon-name">
              Taxon name
            </label>

            <Search size={21} aria-hidden="true" />

            <input
              id="taxon-name"
              type="search"
              placeholder="Tyrannosaurus, Trilobita, Canis..."
              value={taxonName}
              onChange={(event) => setTaxonName(event.target.value)}
            />

            <button
              type="submit"
              disabled={!taxonName.trim() || isLoading}
            >
              {isLoading ? "Searching..." : "Explore"}
            </button>
          </form>
        </div>

        <a
          className="scroll-link"
          href="#explorer"
          aria-label="Go to occurrence explorer"
        >
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
            <p className="result-count">
              {searchResults.length} matches
            </p>
          )}
        </div>

        {isLoading && (
          <p className="search-status">Searching PBDB...</p>
        )}

        {error && (
          <p className="search-error" role="alert">
            {error}
          </p>
        )}

        {hasSearched &&
          !isLoading &&
          !error &&
          searchResults.length === 0 && (
            <p className="search-status">No taxa found.</p>
          )}

        {!isLoading && searchResults.length > 0 && (
          <ol className="taxon-results">
            {searchResults.map((result) => (
              <li key={result.id}>
                <div>
                  <strong>{result.name}</strong>
                  <span>{result.rank ?? "Unranked"}</span>
                </div>

                <span>
                  {result.occurrenceCount ?? "Unknown"} PBDB records
                </span>
              </li>
            ))}
          </ol>
        )}
      </section>
    </main>
  );
}

export default App;