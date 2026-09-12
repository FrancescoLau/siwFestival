import type { Festival } from "../types";

export async function getFestivals(): Promise<Festival[]> {
  const response = await fetch("http://localhost:8080/api/festivals");
  if (!response.ok) {
    throw new Error("Errore durante il caricamento dei festival");
  }
  return (await response.json()) as Festival[];
}