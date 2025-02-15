package ua.foxminded.universitycms.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.foxminded.universitycms.controller.EditionType;
import ua.foxminded.universitycms.domain.Role;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.domain.userdetails.AdminDetails;
import ua.foxminded.universitycms.domain.userdetails.StudentDetails;
import ua.foxminded.universitycms.domain.userdetails.TeacherDetails;
import ua.foxminded.universitycms.domain.userdetails.UserDetails;
import ua.foxminded.universitycms.dto.UserCreateRequest;
import ua.foxminded.universitycms.dto.UserEditRequest;
import ua.foxminded.universitycms.dto.UserResponse;
import ua.foxminded.universitycms.mapper.UserMapper;
import ua.foxminded.universitycms.repository.GroupRepository;
import ua.foxminded.universitycms.repository.RoleRepository;
import ua.foxminded.universitycms.repository.UserDetailsRepository;
import ua.foxminded.universitycms.repository.UserRepository;
import ua.foxminded.universitycms.service.UserService;
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;
import ua.foxminded.universitycms.service.exception.InvalidUserConfigurationException;
import ua.foxminded.universitycms.service.util.PageNumberParser;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepo;
  private final RoleRepository roleRepo;
  private final GroupRepository groupRepo;
  private final UserDetailsRepository detailsRepo;
  private final UserMapper mapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public User saveFromRequest(UserCreateRequest request) {
    User user = mapRolesAndUserDetailsForNewUser(mapper.userCreateRequestToUser(request), request.getRoles(),
        request.getFirstName(), request.getLastName(), request.getGroupName(), request.getJobTitle());

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    userRepo.save(user);
    return user;
  }

  @Override
  @Transactional(readOnly = true)
  public User findById(String id) {
    User user = userRepo.findById(id).orElseThrow(
        () -> new EntityNotFoundRuntimeException("Entity with id=" + id + " doesn't exist."));
    return user;
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserResponseById(String id) {
    UserResponse response = mapper.userToUserResponse(userRepo.findById(id).orElseThrow(
        () -> new EntityNotFoundRuntimeException("Entity with id=" + id + " doesn't exist.")));
    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserResponseByLogin(String login) {
    UserResponse response = mapper.userToUserResponse(userRepo.findByLogin(login).orElseThrow(
        () -> new EntityNotFoundRuntimeException("User with login=" + login + " doesn't exist.")));
    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public User findByLogin(String login) {
    User user = userRepo.findByLogin(login).orElseThrow(
        () -> new EntityNotFoundRuntimeException("User with login=" + login + " doesn't exist."));
    return user;
  }

  @Override
  @Transactional(readOnly = true)
  public UserEditRequest getUserEditRequestById(String id) {
    return mapper.userToUserEditRequest(userRepo.findById(id).orElseThrow(
        () -> new EntityNotFoundRuntimeException("Entity with id=" + id + " doesn't exist.")));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserResponse> getUserResponses(String keyword, Integer itemsPerPage, String pageNumber) {
    if (keyword == null || keyword.isBlank()) {
      return userRepo.findAll(PageRequest.of(PageNumberParser.parse(pageNumber), itemsPerPage, Sort.by("login")))
          .map(mapper::userToUserResponse);
    } else {
      return userRepo.findByLoginContainingOrUserDetailsListLastNameContaining(keyword, keyword,
          PageRequest.of(PageNumberParser.parse(pageNumber), itemsPerPage, Sort.by("login")))
          .map(mapper::userToUserResponse);
    }
  }

  @Override
  @Transactional
  public void editUserFromRequest(String requestedEditionType, UserEditRequest request) {
    EditionType editionType = EditionType.fromString(requestedEditionType);

    if (editionType == EditionType.LOGIN_INFO) {
      editLoginInfoFromRequest(request);
    } else if (editionType == EditionType.ROLES) {
      editRolesAndDetailsFromRequest(request);
    } else {
      editRolesAndDetailsFromRequest(request);
    }
  }

  private void editLoginInfoFromRequest(UserEditRequest request) {
    String password;
    if (request.getPassword() == null || request.getPassword().isBlank()) {
      password = userRepo.findById(request.getId()).orElseThrow(
          () -> new EntityNotFoundRuntimeException("Entity with id=" + request.getId() + " doesn't exist."))
          .getPassword();
    } else {
      password = passwordEncoder.encode(request.getPassword());
    }

    userRepo.editLoginInfoById(request.getLogin(), password, request.getEmail(), request.getId());
  }

  private void editRolesAndDetailsFromRequest(UserEditRequest request) {
    User user = userRepo.findById(request.getId()).orElseThrow(
        () -> new EntityNotFoundRuntimeException("User with id = " + request.getId() + " doesn't exist."));

    if (request.getRoles() == null || request.getRoles().isEmpty()) {
      throw new InvalidUserConfigurationException("User must have at least one role.");
    }

    Set<Role> roles = new HashSet<Role>();
    request.getRoles().forEach(roleName -> {
      roles.add(roleRepo.findByName(roleName).orElseThrow(
          () -> new EntityNotFoundRuntimeException("Role with name = " + roleName + " doesn't exist.")));
    });
    user.setRoles(roles);

    List<UserDetails> userDetailsList = user.getUserDetailsList() == null ? new ArrayList<>()
        : user.getUserDetailsList();

    userDetailsList = changeExistingAndDeleteRedundantDetails(userDetailsList, user, request);
    userDetailsList = addMissingDetails(userDetailsList, user, request);
    user.setUserDetailsList(userDetailsList);
    user.getUserDetailsList().forEach(userDetails -> userDetails.setUser(user));

    userRepo.save(user);
  }

  @Override
  @Transactional
  public void deleteById(String id) {
    userRepo.deleteById(id);
  }

  private User mapRolesAndUserDetailsForNewUser(User user, List<String> roleNames, String firstName, String lastName,
      String groupName, String jobTitle) {

    if (roleNames == null || roleNames.isEmpty()) {
      throw new InvalidUserConfigurationException("User must have at least one role.");
    }

    Set<Role> roles = new HashSet<Role>();
    List<UserDetails> userDetailsList = new ArrayList<UserDetails>();
    roleNames.forEach(roleName -> {
      roles.add(roleRepo.findByName(roleName).orElseThrow(
          () -> new EntityNotFoundRuntimeException("Role with name = " + roleName + " doesn't exist.")));

      if ("ADMINISTRATOR".equals(roleName)) {
        AdminDetails adminDetails = AdminDetails.builder()
            .firstName(firstName)
            .lastName(lastName)
            .jobTitle(jobTitle.isBlank() ? null : jobTitle)
            .user(user).build();
        userDetailsList.add(adminDetails);
      }
      if ("TEACHER".equals(roleName)) {
        TeacherDetails teacherDetails = TeacherDetails.builder()
            .firstName(firstName)
            .lastName(lastName)
            .user(user).build();
        userDetailsList.add(teacherDetails);
      }
      if ("STUDENT".equals(roleName)) {
        StudentDetails studentDetails = StudentDetails.builder()
            .firstName(firstName)
            .lastName(lastName)
            .group(groupName.isBlank() ? null
                : groupRepo.findByName(groupName).orElseThrow(() -> new EntityNotFoundRuntimeException(
                    "Group with name = " + groupName + " doesn't exist.")))
            .user(user).build();
        userDetailsList.add(studentDetails);
      }
    });
    user.setRoles(roles);
    user.setUserDetailsList(userDetailsList);

    return user;
  }

  private List<UserDetails> changeExistingAndDeleteRedundantDetails(List<UserDetails> userDetailsList, User user,
      UserEditRequest request) {
    for (Iterator<UserDetails> iterator = userDetailsList.iterator(); iterator.hasNext();) {
      UserDetails userDetails = iterator.next();
      if (userDetails instanceof AdminDetails) {
        if (user.getRoles().stream().anyMatch(role -> "ADMINISTRATOR".equals(role.getName()))) {
          AdminDetails adminDetails = (AdminDetails) userDetails;
          adminDetails.setFirstName(request.getFirstName());
          adminDetails.setLastName(request.getLastName());
          adminDetails.setJobTitle(request.getJobTitle());
        } else {
          detailsRepo.delete(userDetails);
          iterator.remove();
        }
      } else if (userDetails instanceof TeacherDetails) {
        if (user.getRoles().stream().anyMatch(role -> "TEACHER".equals(role.getName()))) {
          TeacherDetails teacherDetails = (TeacherDetails) userDetails;
          teacherDetails.setFirstName(request.getFirstName());
          teacherDetails.setLastName(request.getLastName());
        } else {
          detailsRepo.delete(userDetails);
          iterator.remove();
        }
      } else {
        if (user.getRoles().stream().anyMatch(role -> "STUDENT".equals(role.getName()))) {
          StudentDetails studentDetails = (StudentDetails) userDetails;
          studentDetails.setFirstName(request.getFirstName());
          studentDetails.setLastName(request.getLastName());
          studentDetails.setGroup(request.getGroupName() == null || request.getGroupName().isBlank() ? null
              : groupRepo.findByName(request.getGroupName()).orElseThrow(() -> new EntityNotFoundRuntimeException(
                  "Group with name = " + request.getGroupName() + " doesn't exist.")));
        } else {
          detailsRepo.delete(userDetails);
          iterator.remove();
        }
      }
    }
    return userDetailsList;
  }

  private List<UserDetails> addMissingDetails(List<UserDetails> userDetailsList, User user, UserEditRequest request) {
    user.getRoles().forEach(role -> {
      if ("ADMINISTRATOR".equals(role.getName())
          && !(userDetailsList.stream().anyMatch(userDetails -> userDetails instanceof AdminDetails))) {
        AdminDetails adminDetails = AdminDetails.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .jobTitle(request.getJobTitle().isBlank() ? null : request.getJobTitle()).build();
        userDetailsList.add(adminDetails);
      } else if ("TEACHER".equals(role.getName())
          && !(userDetailsList.stream().anyMatch(userDetails -> userDetails instanceof TeacherDetails))) {
        TeacherDetails teacherDetails = TeacherDetails.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName()).build();
        userDetailsList.add(teacherDetails);
      } else if ("STUDENT".equals(role.getName())
          && !(userDetailsList.stream().anyMatch(userDetails -> userDetails instanceof StudentDetails))) {
        StudentDetails studentDetails = StudentDetails.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .group(request.getGroupName() == null || request.getGroupName().isBlank() ? null
                : groupRepo.findByName(request.getGroupName()).orElseThrow(() -> new EntityNotFoundRuntimeException(
                    "Group with name = " + request.getGroupName() + " doesn't exist.")))
            .build();
        userDetailsList.add(studentDetails);
      }
    });
    return userDetailsList;
  }
}
