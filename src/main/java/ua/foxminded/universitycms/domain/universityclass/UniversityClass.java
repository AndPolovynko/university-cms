package ua.foxminded.universitycms.domain.universityclass;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.domain.Identifiable;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.repository.config.IDGenerator;

@Entity
@Table(name = "university_classes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UniversityClass implements Identifiable<String> {
  @Id
  @IDGenerator
  @Column(name = "id")
  private String id;
  
  @ManyToOne
  @JoinColumn(name = "type_id", referencedColumnName = "id")
  private UniversityClassType type;
  
  @ManyToOne
  @JoinColumn(name = "venue_id", referencedColumnName = "id")
  private UniversityClassVenue venue;
  
  @ManyToOne
  @JoinColumn(name = "course_id", referencedColumnName = "id")
  private Course course;
  
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "university_classes_groups", 
      joinColumns = @JoinColumn(name = "university_class_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id"))
  private List<Group> groups;
  
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "university_classes_users", 
      joinColumns = @JoinColumn(name = "university_class_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> teachers;
  
  @Column(name = "date_and_time")
  private OffsetDateTime dateTime;
}
