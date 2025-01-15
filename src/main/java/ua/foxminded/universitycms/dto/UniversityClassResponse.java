package ua.foxminded.universitycms.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UniversityClassResponse {
  private String id;
  private String type;
  private String venue;
  private String course;
  private List<String> groupNames;
  private List<String> teacherLogins;
  private String dateTime;
}
