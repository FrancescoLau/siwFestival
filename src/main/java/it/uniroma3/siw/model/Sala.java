package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"nome", "indirizzo"}))
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String indirizzo;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer capienza;
    
    @JsonIgnore
    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL)
    private List<Proiezione> proiezioni;

    public Sala() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    public Integer getCapienza() { return capienza; }
    public void setCapienza(Integer capienza) { this.capienza = capienza; }
    public List<Proiezione> getProiezioni() { return proiezioni; }
    public void setProiezioni(List<Proiezione> proiezioni) { this.proiezioni = proiezioni; }

    @Override
    public int hashCode() {
        return Objects.hash(indirizzo, nome);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sala other = (Sala) obj;
        return Objects.equals(indirizzo, other.indirizzo) && Objects.equals(nome, other.nome);
    }
}