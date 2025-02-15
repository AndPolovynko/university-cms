package ua.foxminded.universitycms.service;

import org.springframework.data.domain.Page;

import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.dto.UserCreateRequest;
import ua.foxminded.universitycms.dto.UserEditRequest;
import ua.foxminded.universitycms.dto.UserResponse;

public interface UserService {
  User saveFromRequest(UserCreateRequest request);

  User findById(String id);
  
  UserResponse getUserResponseById(String id);
  
  UserResponse getUserResponseByLogin(String login);
  
  UserEditRequest getUserEditRequestById(String id);

  User findByLogin(String login);
  
  Page<UserResponse> getUserResponses(String keyword, Integer itemsPerPage, String pageNumber);
  
  void editUserFromRequest(String requestedEditionType, UserEditRequest request);

  void deleteById(String id);
}
