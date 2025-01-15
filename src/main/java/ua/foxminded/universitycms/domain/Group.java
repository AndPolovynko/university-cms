package ua.foxminded.universitycms.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.domain.userdetails.StudentDetails;
import ua.foxminded.universitycms.repository.config.IDGenerator;

@Entity
@Table(name = "groups")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Group implements Identifiable<String> {
  @Id
  @IDGenerator
  @Column(name = "id")
  private String id;

  @Column(name = "name")
  private String name;
  
  @ManyToMany(mappedBy = "groups")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<UniversityClass> classes;
  
  @OneToMany(mappedBy = "group")
  private List<StudentDetails> studentDetailsList;
}
