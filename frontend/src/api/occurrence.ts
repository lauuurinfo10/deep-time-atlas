import type { FossilOccurrence } from "../types/occurrence";

type GetOccurrencesOptions = {
  taxonName: string;
  interval?: string;
  limit?: number;
  offset?: number;
};

export const OCCURRENCE_PAGE_SIZE = 50;

export async function getOccurrences({
  taxonName,
  interval,
  limit = 100,
  offset = 0,
}: GetOccurrencesOptions): Promise<FossilOccurrence[]> {
  const query = new URLSearchParams({
    limit: String(limit),
    offset: String(offset),
  });

  if (interval?.trim()) {
    query.set("interval", interval.trim());
  }

  const response = await fetch(
    `/api/taxa/${encodeURIComponent(taxonName)}/occurrences?${query.toString()}`,
  );

  if (!response.ok) {
    throw new Error(
      `Occurrence request failed with status ${response.status}`,
    );
  }

  const occurrences: FossilOccurrence[] = await response.json();

  return occurrences;
}
