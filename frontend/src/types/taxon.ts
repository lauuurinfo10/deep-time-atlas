export type TaxonSearchResult = {
  id: number;
  name: string;
  rank: string | null;
  occurrenceCount: number | null;
};