package ua.foxminded.universitycms.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UniversityClassTypeResponse {
  private String id;
  private String name;
}
