import { useEffect, useMemo, useRef, useState } from "react";
import { ExternalLink } from "lucide-react";
import {
  LngLatBounds,
  setWorkerUrl,
  type CircleLayerSpecification,
  type GeoJSONSource,
  type SymbolLayerSpecification,
} from "maplibre-gl";
import Map, {
  Layer,
  NavigationControl,
  Popup,
  Source,
  type MapLayerMouseEvent,
  type MapRef,
} from "react-map-gl/maplibre";
import workerUrl from "maplibre-gl/dist/maplibre-gl-worker.mjs?worker&url";
import "maplibre-gl/dist/maplibre-gl.css";
import { getReference } from "../api/reference";
import type { FossilOccurrence } from "../types/occurrence";
import type { BibliographicReference } from "../types/reference";
import "./OccurrenceMap.css";

setWorkerUrl(workerUrl);

const SOURCE_ID = "fossil-occurrences";
const CLUSTER_LAYER_ID = "occurrence-clusters";
const CLUSTER_COUNT_LAYER_ID = "occurrence-cluster-count";
const OCCURRENCE_LAYER_ID = "individual-occurrences";

const clusterLayer: CircleLayerSpecification = {
  id: CLUSTER_LAYER_ID,
  type: "circle",
  source: SOURCE_ID,
  filter: ["has", "point_count"],
  paint: {
    "circle-color": [
      "step",
      ["get", "point_count"],
      "#52705f",
      10,
      "#28736c",
      30,
      "#a6402d",
    ],
    "circle-radius": [
      "step",
      ["get", "point_count"],
      19,
      10,
      25,
      30,
      31,
    ],
    "circle-stroke-color": "#ffffff",
    "circle-stroke-width": 2,
  },
};

const clusterCountLayer: SymbolLayerSpecification = {
  id: CLUSTER_COUNT_LAYER_ID,
  type: "symbol",
  source: SOURCE_ID,
  filter: ["has", "point_count"],
  layout: {
    "text-field": ["get", "point_count_abbreviated"],
    "text-size": 13,
  },
  paint: {
    "text-color": "#ffffff",
  },
};

const occurrenceLayer: CircleLayerSpecification = {
  id: OCCURRENCE_LAYER_ID,
  type: "circle",
  source: SOURCE_ID,
  filter: ["!", ["has", "point_count"]],
  paint: {
    "circle-color": "#b94d35",
    "circle-radius": 8,
    "circle-stroke-color": "#ffffff",
    "circle-stroke-width": 2,
  },
};

type OccurrenceMapProps = {
  occurrences: FossilOccurrence[];
};

type MappedOccurrence = FossilOccurrence & {
  longitude: number;
  latitude: number;
};

function formatAge(occurrence: FossilOccurrence) {
  if (
    occurrence.maxAgeMa !== null &&
    occurrence.minAgeMa !== null
  ) {
    return `${occurrence.maxAgeMa} - ${occurrence.minAgeMa} Ma`;
  }

  return occurrence.earlyInterval ?? "Age unavailable";
}

function OccurrenceMap({ occurrences }: OccurrenceMapProps) {
  const mapRef = useRef<MapRef>(null);
  const referenceRequestId = useRef(0);

  const [selectedOccurrence, setSelectedOccurrence] =
    useState<MappedOccurrence | null>(null);
  const [reference, setReference] =
    useState<BibliographicReference | null>(null);
  const [isReferenceLoading, setIsReferenceLoading] =
    useState(false);
  const [referenceError, setReferenceError] =
    useState<string | null>(null);

  const mappedOccurrences = useMemo(
    () =>
      occurrences.filter(
        (occurrence): occurrence is MappedOccurrence =>
          occurrence.longitude !== null &&
          occurrence.latitude !== null,
      ),
    [occurrences],
  );

  const occurrenceGeoJson = useMemo(
    () => ({
      type: "FeatureCollection" as const,
      features: mappedOccurrences.map((occurrence) => ({
        type: "Feature" as const,
        properties: {
          occurrenceId: occurrence.id,
        },
        geometry: {
          type: "Point" as const,
          coordinates: [
            occurrence.longitude,
            occurrence.latitude,
          ],
        },
      })),
    }),
    [mappedOccurrences],
  );

  useEffect(() => {
    if (mappedOccurrences.length === 0) {
      return;
    }

    const bounds = new LngLatBounds();

    mappedOccurrences.forEach((occurrence) => {
      bounds.extend([
        occurrence.longitude,
        occurrence.latitude,
      ]);
    });

    mapRef.current?.fitBounds(bounds, {
      padding: 70,
      maxZoom: 6,
      duration: 900,
    });
  }, [mappedOccurrences]);

  function closePopup() {
    referenceRequestId.current += 1;
    setSelectedOccurrence(null);
    setReference(null);
    setReferenceError(null);
    setIsReferenceLoading(false);
  }

  async function selectOccurrence(
    occurrence: MappedOccurrence,
  ) {
    const requestId = referenceRequestId.current + 1;
    referenceRequestId.current = requestId;

    setSelectedOccurrence(occurrence);
    setReference(null);
    setReferenceError(null);

    if (occurrence.referenceId === null) {
      setIsReferenceLoading(false);
      return;
    }

    setIsReferenceLoading(true);

    try {
      const result = await getReference(
        occurrence.referenceId,
      );

      if (referenceRequestId.current === requestId) {
        setReference(result);
      }
    } catch {
      if (referenceRequestId.current === requestId) {
        setReferenceError(
          "The bibliographic reference could not be loaded.",
        );
      }
    } finally {
      if (referenceRequestId.current === requestId) {
        setIsReferenceLoading(false);
      }
    }
  }

  async function handleMapClick(event: MapLayerMouseEvent) {
    const feature = event.features?.[0];

    if (!feature || feature.geometry.type !== "Point") {
      return;
    }

    const [longitude, latitude] = feature.geometry.coordinates;

    if (feature.layer.id === CLUSTER_LAYER_ID) {
      closePopup();

      const clusterId = Number(
        feature.properties?.cluster_id,
      );

      const source = mapRef.current?.getSource(
        SOURCE_ID,
      ) as GeoJSONSource | undefined;

      if (!source || !Number.isFinite(clusterId)) {
        return;
      }

      const zoom = await source.getClusterExpansionZoom(
        clusterId,
      );

      mapRef.current?.easeTo({
        center: [longitude, latitude],
        zoom,
        duration: 600,
      });

      return;
    }

    if (feature.layer.id === OCCURRENCE_LAYER_ID) {
      const occurrenceId = Number(
        feature.properties?.occurrenceId,
      );

      const occurrence = mappedOccurrences.find(
        (item) => item.id === occurrenceId,
      );

      if (occurrence) {
        await selectOccurrence(occurrence);
      }
    }
  }

  return (
    <div className="occurrence-map">
      <Map
        ref={mapRef}
        initialViewState={{
          longitude: 0,
          latitude: 20,
          zoom: 1.2,
        }}
        mapStyle="https://tiles.openfreemap.org/styles/liberty"
        interactiveLayerIds={[
          CLUSTER_LAYER_ID,
          OCCURRENCE_LAYER_ID,
        ]}
        onClick={handleMapClick}
        reuseMaps
      >
        <NavigationControl
          position="top-right"
          showCompass={false}
        />

        <Source
          id={SOURCE_ID}
          type="geojson"
          data={occurrenceGeoJson}
          cluster
          clusterMaxZoom={9}
          clusterRadius={52}
        >
          <Layer {...clusterLayer} />
          <Layer {...clusterCountLayer} />
          <Layer {...occurrenceLayer} />
        </Source>

        {selectedOccurrence && (
          <Popup
            longitude={selectedOccurrence.longitude}
            latitude={selectedOccurrence.latitude}
            anchor="top"
            closeOnClick={false}
            maxWidth="360px"
            onClose={closePopup}
          >
            <article className="map-popup">
              <h3>
                {selectedOccurrence.acceptedName ??
                  selectedOccurrence.identifiedName ??
                  `Occurrence ${selectedOccurrence.id}`}
              </h3>

              <p>{formatAge(selectedOccurrence)}</p>

              <p>
                {selectedOccurrence.region ??
                  selectedOccurrence.countryCode ??
                  "Location unavailable"}
              </p>

              <dl>
                <div>
                  <dt>Occurrence</dt>
                  <dd>{selectedOccurrence.id}</dd>
                </div>

                {selectedOccurrence.collectionId !== null && (
                  <div>
                    <dt>Collection</dt>
                    <dd>{selectedOccurrence.collectionId}</dd>
                  </div>
                )}
              </dl>

              <section className="popup-reference">
                <h4>Bibliographic reference</h4>

                {selectedOccurrence.referenceId === null && (
                  <p>No reference is linked to this record.</p>
                )}

                {isReferenceLoading && (
                  <p>Loading reference...</p>
                )}

                {referenceError && (
                  <p className="popup-error">
                    {referenceError}
                  </p>
                )}

                {reference && (
                  <>
                    <p>
                      {reference.formattedCitation ??
                        reference.title ??
                        `Reference ${reference.id}`}
                    </p>

                    {reference.doi ? (
                      <a
                        href={`https://doi.org/${reference.doi}`}
                        target="_blank"
                        rel="noreferrer"
                        onClick={(event) => event.stopPropagation()}
                      >
                        Open publication DOI
                        <ExternalLink size={15} aria-hidden="true" />
                      </a>
                    ) : (
                      <p className="reference-id">
                        PBDB reference ID: {reference.id}. No DOI is available.
                      </p>
                    )}
                  </>
                )}
              </section>
            </article>
          </Popup>
        )}
      </Map>
    </div>
  );
}

export default OccurrenceMap;
