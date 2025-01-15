package ua.foxminded.universitycms.domain.userdetails;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_details")
@DiscriminatorValue("teacher")
@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=false)
public class TeacherDetails extends UserDetails {
  
}
