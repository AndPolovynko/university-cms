package ua.foxminded.universitycms.domain.userdetails;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ua.foxminded.universitycms.domain.Identifiable;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.repository.config.IDGenerator;

@Entity
@Table(name = "user_details")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public abstract class UserDetails implements Identifiable<String> {
  @Id
  @IDGenerator
  @Column(name = "id")
  private String id;
  
  @Column(name = "first_name")
  @NotBlank(message = "User first name cannot be blank.")
  private String firstName;
  
  @Column(name = "last_name")
  @NotBlank(message = "User last name cannot be blank.")
  private String lastName;
  
  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private User user;
}
