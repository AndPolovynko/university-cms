package ua.foxminded.universitycms.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseResponse {
  String id;
  String name;
  String description;
}
