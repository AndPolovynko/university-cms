package ua.foxminded.universitycms.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupResponse {
  String id;
  String name;
}
