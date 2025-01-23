package ua.foxminded.universitycms.domain;

import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.domain.userdetails.UserDetails;
import ua.foxminded.universitycms.repository.config.IDGenerator;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User implements Identifiable<String> {
  @Id
  @IDGenerator
  @Column(name = "id")
  private String id;

  @Column(name = "login")
  @NotBlank(message = "User login cannot be blank.")
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Login can only contain Latin letters, numbers, and underscores.")
  private String login;

  @Column(name = "password")
  private String password;

  @Column(name = "email")
  private String email;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<UserDetails> userDetailsList;

  @ManyToMany(mappedBy = "teachers")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<UniversityClass> classes;
}
