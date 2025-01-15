package ua.foxminded.universitycms.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.dto.GroupCreateRequest;
import ua.foxminded.universitycms.dto.GroupEditRequest;
import ua.foxminded.universitycms.dto.GroupResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GroupMapper {
  GroupResponse groupToGroupResponse(Group group);
  
  GroupEditRequest groupToGroupEditRequest(Group group);
  
  Group groupCreateRequestToGroup(GroupCreateRequest request);
  
  Group groupEditRequestToGroup(GroupEditRequest request);
}
