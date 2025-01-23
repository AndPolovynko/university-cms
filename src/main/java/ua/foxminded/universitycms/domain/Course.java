package ua.foxminded.universitycms.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.repository.config.IDGenerator;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Course implements Identifiable<String> {
  @Id
  @IDGenerator
  @Column(name = "id")
  private String id;
  
  @Column(name = "name")
  @NotBlank(message = "Course name cannot be blank.")
  private String name;
  
  @Column(name = "description")
  private String description;
  
  @OneToMany(mappedBy = "course")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<UniversityClass> classes;
}
