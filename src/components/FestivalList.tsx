import { useEffect, useState } from "react";
import type { Festival } from "../types";
import { getFestivals } from "../services/festivalService";
import { FestivalCard } from "./FestivalCard";

export function FestivalList() {
  const [festivals, setFestivals] = useState<Festival[]>([]);
  const [query, setQuery] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getFestivals()
      .then((data) => {
        console.log("Dati ricevuti da Spring Boot:", data);
        // Garantisce che festivals sia sempre un array
        if (Array.isArray(data)) {
          setFestivals(data);
        } else {
          setFestivals([]);
        }
        setLoading(false);
      })
      .catch((err) => {
        console.error("Errore fetch:", err);
        setError(err.message || "Errore di connessione al server");
        setLoading(false);
      });
  }, []);

  const festivalFiltrati = Array.isArray(festivals)
    ? festivals.filter((f) => {
        const testoRicerca = query.toLowerCase();
        const matchNome = f?.nome ? f.nome.toLowerCase().includes(testoRicerca) : false;
        const matchCitta = f?.citta ? f.citta.toLowerCase().includes(testoRicerca) : false;
        return matchNome || matchCitta;
      })
    : [];

  return (
    <div style={{ maxWidth: "900px", margin: "2rem auto", padding: "0 1rem", fontFamily: "sans-serif" }}>
      <h1 style={{ textAlign: "center", color: "#0f172a" }}>🎪 Festival in Programma</h1>

      {loading && <p style={{ textAlign: "center", color: "#64748b" }}>Caricamento festival in corso...</p>}
      
      {error && (
        <div style={{ textAlign: "center", padding: "1rem", backgroundColor: "#fef2f2", border: "1px solid #f87171", borderRadius: "6px", color: "#b91c1c" }}>
          <p><strong>Impossibile caricare i dati:</strong> {error}</p>
          <small>Verifica che Spring Boot sia avviato su http://localhost:8080</small>
        </div>
      )}

      {!loading && !error && (
        <>
          <div style={{ margin: "1.5rem 0", display: "flex", justifyContent: "center" }}>
            <input
              type="text"
              placeholder="Filtra per nome o per città..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              style={{
                width: "100%",
                maxWidth: "420px",
                padding: "0.6rem 1rem",
                fontSize: "1rem",
                borderRadius: "6px",
                border: "1px solid #cbd5e1",
                outline: "none"
              }}
            />
          </div>

          {festivalFiltrati.length === 0 ? (
            <p style={{ textAlign: "center", color: "#64748b" }}>Nessun festival trovato.</p>
          ) : (
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))", gap: "1rem" }}>
              {festivalFiltrati.map((fest) => (
                <FestivalCard key={fest.id} festival={fest} />
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
}