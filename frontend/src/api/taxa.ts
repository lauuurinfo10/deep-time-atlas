import type { TaxonSearchResult } from "../types/taxon";

export async function searchTaxa(
  name: string,
): Promise<TaxonSearchResult[]> {
  const query = new URLSearchParams({ name });

  const response = await fetch(
    `/api/taxa/search?${query.toString()}`,
  );

  if (!response.ok) {
    throw new Error(
      `Taxon search failed with status ${response.status}`,
    );
  }

  const results: TaxonSearchResult[] = await response.json();

  return results;
}