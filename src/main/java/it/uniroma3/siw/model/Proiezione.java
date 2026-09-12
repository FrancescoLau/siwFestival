package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"data", "ora", "sala_id"}))
public class Proiezione {

    public enum StatoProiezione {
        SCHEDULED, COMPLETED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate data;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime ora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoProiezione stato;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Film film;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Sala sala;

    public Proiezione() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getOra() { return ora; }
    public void setOra(LocalTime ora) { this.ora = ora; }
    public StatoProiezione getStato() { return stato; }
    public void setStato(StatoProiezione stato) { this.stato = stato; }
    public Festival getFestival() { return festival; }
    public void setFestival(Festival festival) { this.festival = festival; }
    public Film getFilm() { return film; }
    public void setFilm(Film film) { this.film = film; }
    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }

    @Override
    public int hashCode() {
        return Objects.hash(data, ora, sala);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Proiezione other = (Proiezione) obj;
        return Objects.equals(data, other.data) && Objects.equals(ora, other.ora) && Objects.equals(sala, other.sala);
    }
}