export type BibliographicReference = {
  id: number;
  title: string | null;
  publicationYear: string | null;
  publicationType: string | null;
  publicationTitle: string | null;
  formattedCitation: string | null;
  language: string | null;
  doi: string | null;
  sourceUrl: string;
};