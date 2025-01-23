package ua.foxminded.universitycms.domain.userdetails;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_details")
@DiscriminatorValue("admin")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=false)
public class AdminDetails extends UserDetails {
  @Column(name = "job_title")
  @NotBlank(message = "Admin job title must be specified.")
  private String jobTitle;
}
