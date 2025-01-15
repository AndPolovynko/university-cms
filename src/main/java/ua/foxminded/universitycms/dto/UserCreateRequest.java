package ua.foxminded.universitycms.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserCreateRequest {
  private String login;
  private String password;
  private String email;
  private List<String> roles;
  private String firstName;
  private String lastName;
  private String jobTitle;
  private String groupName;
}
