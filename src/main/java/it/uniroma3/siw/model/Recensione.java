package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"utente_id", "film_id"}))
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer voto;

    @NotBlank
    @Column(length = 2000, nullable = false)
    private String testo;

    
    @Column(nullable = false)
    private LocalDate data;

    
    @ManyToOne(fetch = FetchType.EAGER)
    private User utente;

    
    @ManyToOne(fetch = FetchType.LAZY)
    private Film film;

    public Recensione() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }
    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public User getUtente() { return utente; }
    public void setUtente(User utente) { this.utente = utente; }
    public Film getFilm() { return film; }
    public void setFilm(Film film) { this.film = film; }

    @Override
    public int hashCode() {
        return Objects.hash(film, utente);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recensione other = (Recensione) obj;
        return Objects.equals(film, other.film) && Objects.equals(utente, other.utente);
    }
}