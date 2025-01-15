package ua.foxminded.universitycms.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ua.foxminded.universitycms.domain.Role;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.domain.userdetails.AdminDetails;
import ua.foxminded.universitycms.domain.userdetails.StudentDetails;
import ua.foxminded.universitycms.domain.userdetails.UserDetails;
import ua.foxminded.universitycms.dto.UserCreateRequest;
import ua.foxminded.universitycms.dto.UserEditRequest;
import ua.foxminded.universitycms.dto.UserResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
  @Mapping(target = "firstName", source = "userDetailsList", qualifiedByName = "firstNameFromUserDetilsList")
  @Mapping(target = "lastName", source = "userDetailsList", qualifiedByName = "lastNameFromUserDetilsList")
  @Mapping(target = "jobTitle", source = "userDetailsList", qualifiedByName = "jobTitleFromUserDetilsList")
  @Mapping(target = "groupName", source = "userDetailsList", qualifiedByName = "groupNameFromUserDetilsList")
  public abstract UserResponse userToUserResponse(User user);
  
  @Mapping(target = "firstName", source = "userDetailsList", qualifiedByName = "firstNameFromUserDetilsList")
  @Mapping(target = "lastName", source = "userDetailsList", qualifiedByName = "lastNameFromUserDetilsList")
  @Mapping(target = "jobTitle", source = "userDetailsList", qualifiedByName = "jobTitleFromUserDetilsList")
  @Mapping(target = "groupName", source = "userDetailsList", qualifiedByName = "groupNameFromUserDetilsList")
  public abstract UserEditRequest userToUserEditRequest(User user);
  
  public abstract List<UserResponse> usersToUserResponses(List<User> users);
  
  @Mapping(target = "roles", ignore = true)
  public abstract User userCreateRequestToUser(UserCreateRequest dto);

  @Mapping(target = "roles", ignore = true)
  public abstract User userEditRequestToUser(UserEditRequest dto);

  @IterableMapping(qualifiedByName = "roleToRoleName")
  public abstract List<String> rolesToReloNames(Set<Role> roles);

  @Named("roleToRoleName")
  default String roleToRoleName(Role role) {
    return role != null ? role.getName() : null;
  }

  @Named("firstNameFromUserDetilsList")
  default String firstNameFromUserDetilsList(List<UserDetails> userDetailsList) {
    return userDetailsList != null && !userDetailsList.isEmpty() ? userDetailsList.get(0).getFirstName() : null;
  }

  @Named("lastNameFromUserDetilsList")
  default String lastNameFromUserDetilsList(List<UserDetails> userDetailsList) {
    return userDetailsList != null && !userDetailsList.isEmpty() ? userDetailsList.get(0).getLastName() : null;
  }

  @Named("jobTitleFromUserDetilsList")
  default String jobTitleFromUserDetilsList(List<UserDetails> userDetailsList) {
    if (userDetailsList != null && !userDetailsList.isEmpty()) {
      for (UserDetails userDetails : userDetailsList) {
        if (userDetails instanceof AdminDetails) {
          AdminDetails adminDetails = (AdminDetails) userDetails;
          return adminDetails.getJobTitle();
        }
      }
    }
    return null;
  }

  @Named("groupNameFromUserDetilsList")
  default String groupNameFromUserDetilsList(List<UserDetails> userDetailsList) {
    if (userDetailsList != null && !userDetailsList.isEmpty()) {
      for (UserDetails userDetails : userDetailsList) {
        if (userDetails instanceof StudentDetails) {
          StudentDetails studentDetails = (StudentDetails) userDetails;
          return studentDetails.getGroup() == null ? null : studentDetails.getGroup().getName();
        }
      }
    }
    return null;
  }
}
