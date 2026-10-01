import type { BibliographicReference } from "../types/reference";

export async function getReference(
  referenceId: number,
): Promise<BibliographicReference> {
  const response = await fetch(
    `/api/references/${referenceId}`,
  );

  if (!response.ok) {
    throw new Error(
      `Reference request failed with status ${response.status}`,
    );
  }

  const reference: BibliographicReference =
    await response.json();

  return reference;
}