package it.uniroma3.siw.model;

import java.util.Objects;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Credentials {
  public static String DEFAULT_ROLE = "DEFAULT";
  public static String ADMIN_ROLE = "ADMIN";
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  
  @NotBlank
  @Column(nullable = false, unique = true)
  private String username;
  
  @NotBlank
  @Size(min = 4)
  @Column(nullable = false, unique = false)
  private String password;
  
  private String role;
  
  @Valid // Attiva la validazione in cascata sull'oggetto User correlato
  @OneToOne(cascade = CascadeType.ALL)
  private User user;
  
  /* Getter e Setter */
  public static String getDefaultRole() { return DEFAULT_ROLE; }
  public static void setDefaultRole(String defaultRole) { DEFAULT_ROLE = defaultRole; }
  public static String getAdminRole() { return ADMIN_ROLE; }
  public static void setAdminRole(String adminRole) { ADMIN_ROLE = adminRole; }
  
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  @Override
  public int hashCode() { return Objects.hash(username); }

  @Override
  public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Credentials other = (Credentials) obj;
      return Objects.equals(username, other.username);
  }
}