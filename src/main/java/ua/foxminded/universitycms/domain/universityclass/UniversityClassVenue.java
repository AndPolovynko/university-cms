package ua.foxminded.universitycms.domain.universityclass;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.foxminded.universitycms.domain.Identifiable;
import ua.foxminded.universitycms.repository.config.IDGenerator;

@Entity
@Table(name = "university_class_venues")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UniversityClassVenue implements Identifiable<String> {
  @Id
  @IDGenerator
  @Column(name = "id")
  private String id;
  
  @Column(name = "name")
  private String name;
  
  @Column(name = "description")
  private String description;
  
  @OneToMany(mappedBy = "venue", fetch = FetchType.EAGER)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<UniversityClass> classes;
}
