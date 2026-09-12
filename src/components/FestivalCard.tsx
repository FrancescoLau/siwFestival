import type { Festival } from "../types";

interface FestivalCardProps {
  festival: Festival;
}

export function FestivalCard({ festival }: FestivalCardProps) {
  return (
    <div
      style={{
        border: "1px solid #cbd5e1",
        borderRadius: "8px",
        padding: "1.2rem",
        backgroundColor: "#ffffff",
        boxShadow: "0 2px 4px rgba(0,0,0,0.05)"
      }}
    >
      <h3 style={{ margin: "0 0 0.5rem 0", color: "#0f172a" }}>{festival.nome}</h3>
      <p style={{ margin: "0.25rem 0", color: "#475569" }}>
        <strong>Città:</strong> {festival.citta}
      </p>
      <p style={{ margin: "0.25rem 0", color: "#475569" }}>
        <strong>Anno:</strong> {festival.anno}
      </p>
      <p style={{ margin: "0.25rem 0", color: "#475569" }}>
        <strong>Periodo:</strong> {festival.dataInizio} — {festival.dataFine}
      </p>
      {festival.descrizione && (
        <p style={{ margin: "0.5rem 0 0 0", color: "#64748b", fontStyle: "italic" }}>
          {festival.descrizione}
        </p>
      )}
    </div>
  );
}