package ua.foxminded.universitycms.service;

import org.springframework.data.domain.Page;

import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.dto.GroupCreateRequest;
import ua.foxminded.universitycms.dto.GroupEditRequest;
import ua.foxminded.universitycms.dto.GroupResponse;

public interface GroupService {
  Group saveFromRequest(GroupCreateRequest request);
  
  GroupResponse getGroupResponseById(String id);

  Group findByName(String name);
  
  GroupEditRequest getGroupEditRequestById(String id);
  
  Page<GroupResponse> getGroupResponses(String keyword, Integer itemsPerPage, Integer pageNumber);
  
  void editFromRequest(GroupEditRequest request);

  void deleteById(String id);
}
