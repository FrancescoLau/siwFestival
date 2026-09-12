package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"nome", "cognome", "dataNascita"}))
public class Regista {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String cognome;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate dataNascita;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = true)
    private LocalDate dataMorte;

    private String nazionalita;

    @OneToMany(mappedBy = "regista")
    private List<Film> filmDiretti;

    public Regista() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }
    public LocalDate getDataMorte() { return dataMorte; }
    public void setDataMorte(LocalDate dataMorte) { this.dataMorte = dataMorte; }
    public String getNazionalita() { return nazionalita; }
    public void setNazionalita(String nazionalita) { this.nazionalita = nazionalita; }
    public List<Film> getFilmDiretti() { return filmDiretti; }
    public void setFilmDiretti(List<Film> filmDiretti) { this.filmDiretti = filmDiretti; }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome, dataNascita);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Regista other = (Regista) obj;
        return Objects.equals(nome, other.nome) && Objects.equals(cognome, other.cognome)
                && Objects.equals(dataNascita, other.dataNascita);
    }
}