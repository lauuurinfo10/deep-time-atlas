export type FossilOccurrence = {
  id: number;
  collectionId: number | null;
  identifiedName: string | null;
  acceptedName: string | null;
  acceptedRank: string | null;
  earlyInterval: string | null;
  lateInterval: string | null;
  maxAgeMa: number | null;
  minAgeMa: number | null;
  referenceId: number | null;
  longitude: number | null;
  latitude: number | null;
  countryCode: string | null;
  region: string | null;
  coordinateBasis: string | null;
  coordinatePrecision: string | null;
};