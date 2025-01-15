package ua.foxminded.universitycms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import ua.foxminded.universitycms.domain.Group;
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
import ua.foxminded.universitycms.service.exception.EntityNotFoundRuntimeException;
import ua.foxminded.universitycms.service.exception.InvalidUserConfigurationException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
  @Mock
  UserRepository userRepo;
  @Mock
  RoleRepository roleRepo;
  @Mock
  GroupRepository groupRepo;
  @Mock
  UserDetailsRepository detailsRepo;
  @Mock
  UserMapper mapper;
  @Mock
  PasswordEncoder encoder;

  @InjectMocks
  UserServiceImpl service;

  @Captor
  ArgumentCaptor<User> userCaptor;
  @Captor
  ArgumentCaptor<Role> roleCaptor;

  @Test
  void saveFromRequestShouldCallMapper() {
    when(mapper.userCreateRequestToUser(any(UserCreateRequest.class))).thenReturn(getBasicUser());
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));
    when(groupRepo.findByName("the Hispaniola"))
        .thenReturn(Optional.of(Group.builder().name("the Hispaniola").build()));

    service.saveFromRequest(getUserCreateRequest());

    verify(mapper, atLeastOnce()).userCreateRequestToUser(getUserCreateRequest());
  }

  @Test
  void saveFromRequestShouldCallUserRepoWithExpectedUser() {
    when(encoder.encode(any())).thenReturn("password");
    when(mapper.userCreateRequestToUser(any(UserCreateRequest.class))).thenReturn(getBasicUser());
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));
    when(groupRepo.findByName("the Hispaniola"))
        .thenReturn(Optional.of(Group.builder().name("the Hispaniola").build()));

    User expectedUser = getUser();

    service.saveFromRequest(getUserCreateRequest());

    verify(userRepo).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void saveFromRequestShouldCallUserRepoWithExpectedUserIfJobTitleInAdminDetailsIsBlank() {
    when(encoder.encode(any())).thenReturn("password");
    when(mapper.userCreateRequestToUser(any(UserCreateRequest.class))).thenReturn(getBasicUser());
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));
    when(groupRepo.findByName("the Hispaniola"))
        .thenReturn(Optional.of(Group.builder().name("the Hispaniola").build()));

    User expectedUser = getUser();
    List<UserDetails> userDetailsList = new ArrayList<>();
    userDetailsList.add(AdminDetails.builder()
        .firstName("John")
        .lastName("Silver")
        .jobTitle(null).build());
    userDetailsList.add(TeacherDetails.builder()
        .firstName("John")
        .lastName("Silver").build());
    userDetailsList.add(StudentDetails.builder()
        .firstName("John")
        .lastName("Silver")
        .group(Group.builder().name("the Hispaniola").build()).build());
    expectedUser.setUserDetailsList(userDetailsList);

    UserCreateRequest userCreateRequest = getUserCreateRequest();
    userCreateRequest.setJobTitle("");
    service.saveFromRequest(userCreateRequest);

    verify(userRepo).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void saveFromRequestShouldCallUserRepoWithExpectedUserIfGroupNameInSTUDENTDetailsIsBlank() {
    when(encoder.encode(any())).thenReturn("password");
    when(mapper.userCreateRequestToUser(any(UserCreateRequest.class))).thenReturn(getBasicUser());
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));

    User expectedUser = getUser();
    List<UserDetails> userDetailsList = new ArrayList<>();
    userDetailsList.add(AdminDetails.builder()
        .firstName("John")
        .lastName("Silver")
        .jobTitle("Gentleman of Fortune").build());
    userDetailsList.add(TeacherDetails.builder()
        .firstName("John")
        .lastName("Silver").build());
    userDetailsList.add(StudentDetails.builder()
        .firstName("John")
        .lastName("Silver")
        .group(null).build());
    expectedUser.setUserDetailsList(userDetailsList);

    UserCreateRequest userCreateRequest = getUserCreateRequest();
    userCreateRequest.setGroupName("");
    service.saveFromRequest(userCreateRequest);

    verify(userRepo).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void saveFromRequestShouldThrowInvalidUserConfigurationExceptionIfUserRequestRolesListIsNull() {
    UserCreateRequest createRequest = getUserCreateRequest();
    createRequest.setRoles(null);

    assertThrows(InvalidUserConfigurationException.class, () -> service.saveFromRequest(createRequest));
  }

  @Test
  void saveFromRequestShouldThrowInvalidUserConfigurationExceptionIfUserRequestRolesListIsEmpty() {
    UserCreateRequest createRequest = getUserCreateRequest();
    createRequest.setRoles(new ArrayList<String>());

    assertThrows(InvalidUserConfigurationException.class, () -> service.saveFromRequest(createRequest));
  }

  @Test
  void saveFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfRoleWithAnyOfRoleNamesDoesNotExist() {
    when(mapper.userCreateRequestToUser(any(UserCreateRequest.class))).thenReturn(getBasicUser());

    UserCreateRequest userCreateRequest = getUserCreateRequest();
    userCreateRequest.setRoles(List.of("test-role-name"));

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.saveFromRequest(userCreateRequest));

    assertThat(exception.getMessage()).contains("test-role-name");
  }

  @Test
  void saveFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfGroupWithGroupNameDoesNotExist() {
    when(mapper.userCreateRequestToUser(any(UserCreateRequest.class))).thenReturn(getBasicUser());
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));

    UserCreateRequest userCreateRequest = getUserCreateRequest();
    userCreateRequest.setGroupName("test-group-name");

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.saveFromRequest(userCreateRequest));

    assertThat(exception.getMessage()).contains("test-group-name");
  }

  @Test
  void findByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenIdDoesNotExist() {
    when(userRepo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.findById("test-id"));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void findByIdShouldCallRepositoryMethod() {
    when(userRepo.findById(any())).thenReturn(Optional.of(getEmptyUser()));

    service.findById("id");

    verify(userRepo, atLeastOnce()).findById("id");
  }

  @Test
  void findByIdShouldReturnRepositoryMethodOutput() {
    when(userRepo.findById(any())).thenReturn(Optional.of(getEmptyUser()));

    assertThat(service.findById("id").equals(getEmptyUser()));
  }

  @Test
  void getUserResponseByIdShouldCallMapper() {
    when(mapper.userToUserResponse(any(User.class))).thenReturn(getUserResponse());
    when(userRepo.findById(any())).thenReturn(Optional.of(getUser()));

    service.getUserResponseById("");

    verify(mapper, atLeastOnce()).userToUserResponse(any(User.class));
  }

  @Test
  void getUserResponseByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenIdDoesNotExist() {
    when(userRepo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getUserResponseById("test-id"));

    assertThat(exception.getMessage()).contains("test-id");
  }
  
  @Test
  void getUserResponseByLoginShouldCallMapper() {
    when(mapper.userToUserResponse(any(User.class))).thenReturn(getUserResponse());
    when(userRepo.findByLogin(any())).thenReturn(Optional.of(getUser()));

    service.getUserResponseByLogin("");

    verify(mapper, atLeastOnce()).userToUserResponse(any(User.class));
  }

  @Test
  void getUserResponseByLoginShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenLoginDoesNotExist() {
    when(userRepo.findByLogin(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getUserResponseByLogin("test-login"));

    assertThat(exception.getMessage()).contains("test-login");
  }

  @Test
  void getUserEditRequestByIdShouldCallMapper() {
    UserEditRequest request = UserEditRequest.builder()
        .id("id").build();

    when(userRepo.findById(any())).thenReturn(Optional.of(getEmptyUser()));
    when(mapper.userToUserEditRequest(any(User.class))).thenReturn(request);

    service.getUserEditRequestById("id");

    verify(mapper, atLeastOnce()).userToUserEditRequest(getEmptyUser());
  }

  @Test
  void getUserEditRequestByIdShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenIdDoesNotExist() {
    when(userRepo.findById(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.getUserEditRequestById("test-id"));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void findByLoginShouldThrowEntityNotFoundRuntimeExceptionIfEntityWithGivenLoginDoesNotExist() {
    when(userRepo.findByLogin(anyString())).thenReturn(Optional.empty());

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.findByLogin("test-login"));

    assertThat(exception.getMessage()).contains("test-login");
  }

  @Test
  void findByLoginShouldCallRepositoryMethod() {
    when(userRepo.findByLogin(any())).thenReturn(Optional.of(getEmptyUser()));

    service.findByLogin("login");

    verify(userRepo, atLeastOnce()).findByLogin("login");
  }

  @Test
  void findByLoginShouldReturnRepositoryMethodOutput() {
    when(userRepo.findByLogin(any())).thenReturn(Optional.of(getEmptyUser()));

    assertThat(service.findByLogin("login").equals(getEmptyUser()));
  }

  @Test
  void getUserResponsesShouldCallMapperIfKeywordIsNull() {
    when(userRepo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<User>(getUsers()));

    service.getUserResponses(null, 1, 0);

    verify(mapper, atLeastOnce()).userToUserResponse(any());
  }

  @Test
  void getUserResponsesShouldCallRepoFindAllIfKeywordIsNull() {
    when(userRepo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<User>(getUsers()));

    service.getUserResponses(null, 1, 0);

    verify(userRepo, atLeastOnce()).findAll(any(PageRequest.class));
  }
  
  @Test
  void getUserResponsesShouldCallRepoFindAllIfKeywordIsBlank() {
    when(userRepo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<User>(getUsers()));

    service.getUserResponses("", 1, 0);

    verify(userRepo, atLeastOnce()).findAll(any(PageRequest.class));
  }

  @Test
  void getUserResponsesShouldCallMapperIfKeywordIsNotNull() {
    when(userRepo.findByLoginContainingOrUserDetailsListLastNameContaining(any(), any(), any(PageRequest.class)))
        .thenReturn(new PageImpl<User>(getUsers()));

    service.getUserResponses("keyword", 1, 0);

    verify(mapper, atLeastOnce()).userToUserResponse(any());
  }

  @Test
  void getUserResponsesShouldCallRepoFindByLoginContainingOrUserDetailsListLastNameContainingIfKeywordIsNotNull() {
    when(userRepo.findByLoginContainingOrUserDetailsListLastNameContaining(any(), any(), any(PageRequest.class)))
        .thenReturn(new PageImpl<User>(getUsers()));

    service.getUserResponses("keyword", 1, 0);

    verify(userRepo, atLeastOnce()).findByLoginContainingOrUserDetailsListLastNameContaining(any(), any(),
        any(PageRequest.class));
  }

  @Test
  void editLoginInfoFromRequestShouldCallRepository() {
    when(encoder.encode("password")).thenReturn("password");
    doNothing().when(userRepo).editLoginInfoById(any(), any(), any(), any());

    service.editLoginInfoFromRequest(getUserEditRequest());

    verify(userRepo, atLeastOnce()).editLoginInfoById("login", "password", "email", "id");
  }

  @Test
  void editLoginInfoFromRequestShouldCallRepositoryFindByIdIfRequestPasswordIsNull() {
    User user = getEmptyUser();
    user.setPassword("password");

    when(userRepo.findById("id")).thenReturn(Optional.of(user));
    doNothing().when(userRepo).editLoginInfoById(any(), any(), any(), any());

    UserEditRequest request = getUserEditRequest();
    request.setPassword(null);
    service.editLoginInfoFromRequest(request);

    verify(userRepo, atLeastOnce()).editLoginInfoById("login", "password", "email", "id");
  }

  @Test
  void editLoginInfoFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfPasswordIsBlankAndUserDoesNotExist() {
    User user = getEmptyUser();
    user.setPassword("password");

    when(userRepo.findById("test-id")).thenReturn(Optional.empty());

    UserEditRequest request = getUserEditRequest();
    request.setId("test-id");
    request.setPassword("");

    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.editLoginInfoFromRequest(request));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void editRolesAndDetailsFromRequestShouldCallUserRepuserRepooFindById() {
    when(userRepo.findById("id")).thenReturn(Optional.of(getUser()));
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));
    when(groupRepo.findByName("the Hispaniola"))
        .thenReturn(Optional.of(Group.builder().name("the Hispaniola").build()));

    service.editRolesAndDetailsFromRequest(getUserEditRequest());

    verify(userRepo, atLeastOnce()).findById("id");
  }

  @Test
  void editRolesAndDetailsFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfUserWithGivenIdDoesNotExist() {
    when(userRepo.findById("test-id")).thenReturn(Optional.empty());

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setId("test-id");
    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.editRolesAndDetailsFromRequest(editRequest));

    assertThat(exception.getMessage()).contains("test-id");
  }

  @Test
  void editRolesAndDetailsFromRequestShouldThrowInvalidUserConfigurationExceptionIfUserRequestRolesListIsNull() {
    when(userRepo.findById("id")).thenReturn(Optional.of(getEmptyUser()));

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setRoles(null);
    assertThrows(InvalidUserConfigurationException.class, () -> service.editRolesAndDetailsFromRequest(editRequest));
  }

  @Test
  void editRolesAndDetailsFromRequestShouldThrowInvalidUserConfigurationExceptionIfUserRequestRolesListIsEmpty() {
    when(userRepo.findById("id")).thenReturn(Optional.of(getEmptyUser()));

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setRoles(new ArrayList<String>());
    assertThrows(InvalidUserConfigurationException.class, () -> service.editRolesAndDetailsFromRequest(editRequest));
  }

  @Test
  void editRolesAndDetailsFromRequestShouldCallRoleRepoFindByName() {
    when(userRepo.findById("id")).thenReturn(Optional.of(getUser()));
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));
    when(groupRepo.findByName("the Hispaniola"))
        .thenReturn(Optional.of(Group.builder().name("the Hispaniola").build()));

    service.editRolesAndDetailsFromRequest(getUserEditRequest());

    verify(roleRepo, atLeastOnce()).findByName("ADMINISTRATOR");
    verify(roleRepo, atLeastOnce()).findByName("TEACHER");
    verify(roleRepo, atLeastOnce()).findByName("STUDENT");
  }

  @Test
  void editRolesAndDetailsFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfRoleWithAnyOfGivenNamesDoesNotExist() {
    when(userRepo.findById("id")).thenReturn(Optional.of(getUser()));
    when(roleRepo.findByName("test-role-name")).thenReturn(Optional.empty());

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setRoles(List.of("test-role-name"));
    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.editRolesAndDetailsFromRequest(editRequest));

    assertThat(exception.getMessage()).contains("test-role-name");
  }

  @Test
  void editRolesAndDetailsFromRequestShouldCreateUserDetailsListIfItIsNull() {
    User user = getUser();
    user.setUserDetailsList(null);
    when(userRepo.findById("id")).thenReturn(Optional.of(user));
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));
    when(groupRepo.findByName("the Hispaniola"))
        .thenReturn(Optional.of(Group.builder().name("the Hispaniola").build()));

    service.editRolesAndDetailsFromRequest(getUserEditRequest());

    verify(userRepo).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getUserDetailsList().isEmpty());
  }

  @Test
  void editRolesAndDetailsFromRequestShouldThrowEntityNotFoundRuntimeExceptionIfGroupWithGivenNameDoesNotExist() {
    when(userRepo.findById("id")).thenReturn(Optional.of(getUser()));
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));
    when(groupRepo.findByName("test-group-name")).thenReturn(Optional.empty());

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setGroupName("test-group-name");
    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.editRolesAndDetailsFromRequest(editRequest));

    assertThat(exception.getMessage()).contains("test-group-name");
  }

  @Test
  void editRolesAndDetailsFromRequestShouldReturnUserWithNullInGroupFieldIfGroupNameIsBlank() {
    when(userRepo.findById("id")).thenReturn(Optional.of(getUser()));
    doNothing().when(detailsRepo).delete(any());
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setRoles(List.of("STUDENT"));
    editRequest.setGroupName("");
    service.editRolesAndDetailsFromRequest(editRequest);

    verify(userRepo).save(userCaptor.capture());
    StudentDetails studentDetails = (StudentDetails) userCaptor.getValue().getUserDetailsList().get(0);
    assertThat(studentDetails.getGroup()).isNull();
  }

  @Test
  void editRolesAndDetailsFromRequestShouldReturnUserWithNullInGroupFieldIfGroupNameIsNull() {
    User user = getUser();
    user.setUserDetailsList(null);
    when(userRepo.findById("id")).thenReturn(Optional.of(user));
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setGroupName(null);
    service.editRolesAndDetailsFromRequest(editRequest);

    verify(userRepo).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getUserDetailsList()).isNotEmpty();
  }

  @Test
  void editRolesAndDetailsFromRequestShouldCreateDetailsIfUserHasLessRoles() {
    User user = getUser();
    when(userRepo.findById("id")).thenReturn(Optional.of(user));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setRoles(List.of("TEACHER"));
    service.editRolesAndDetailsFromRequest(editRequest);

    verify(userRepo).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getUserDetailsList()).isEqualTo(List.of(TeacherDetails.builder()
        .firstName("John")
        .lastName("Silver").build()));
  }

  @Test
  void editRolesAndDetailsFromRequestShouldCreateDetailsIfUserHasNewRoles() {
    User user = getUser();
    user.setUserDetailsList(null);
    when(userRepo.findById("id")).thenReturn(Optional.of(user));
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setGroupName("");
    editRequest.setJobTitle("");
    service.editRolesAndDetailsFromRequest(editRequest);

    verify(userRepo).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getUserDetailsList()).isNotEmpty();
  }

  @Test
  void editRolesAndDetailsFromRequestShouldCreateDetailsIfUserHasNewRolesAndRequestGroupNameIsNull() {
    User user = getUser();
    when(userRepo.findById("id")).thenReturn(Optional.of(user));
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setGroupName(null);
    editRequest.setJobTitle("");
    service.editRolesAndDetailsFromRequest(editRequest);

    verify(userRepo).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getUserDetailsList()).isNotEmpty();
  }

  @Test
  void editRolesAndDetailsFromRequestShouldThrowEntityNotFoundRuntimeExceptionDuringDetailsCreationIfGroupWithGivenNameDoesNotExist() {
    User user = getUser();
    user.setUserDetailsList(null);
    when(userRepo.findById("id")).thenReturn(Optional.of(user));
    when(roleRepo.findByName("ADMINISTRATOR")).thenReturn(Optional.of(Role.builder().name("ADMINISTRATOR").build()));
    when(roleRepo.findByName("TEACHER")).thenReturn(Optional.of(Role.builder().name("TEACHER").build()));
    when(roleRepo.findByName("STUDENT")).thenReturn(Optional.of(Role.builder().name("STUDENT").build()));
    when(groupRepo.findByName("test-group-name")).thenReturn(Optional.empty());

    UserEditRequest editRequest = getUserEditRequest();
    editRequest.setGroupName("test-group-name");
    EntityNotFoundRuntimeException exception = assertThrows(EntityNotFoundRuntimeException.class,
        () -> service.editRolesAndDetailsFromRequest(editRequest));

    assertThat(exception.getMessage()).contains("test-group-name");
  }

  @Test
  void deleteByIdShouldCallRepositoryMethod() {
    doNothing().when(userRepo).deleteById(any());

    service.deleteById("id");

    verify(userRepo, atLeastOnce()).deleteById("id");
  }

  private UserResponse getUserResponse() {
    List<String> roles = new ArrayList<>();
    roles.add("admin");
    roles.add("teacher");
    roles.add("student");

    return UserResponse.builder()
        .login("login")
        .email("email")
        .roles(roles)
        .firstName("John")
        .lastName("Silver")
        .groupName("the Hispaniola")
        .jobTitle("Gentleman of Fortune").build();
  }

  private UserCreateRequest getUserCreateRequest() {
    List<String> roles = new ArrayList<>();
    roles.add("ADMINISTRATOR");
    roles.add("TEACHER");
    roles.add("STUDENT");

    return UserCreateRequest.builder()
        .login("login")
        .password("password")
        .email("email")
        .roles(roles)
        .firstName("John")
        .lastName("Silver")
        .groupName("the Hispaniola")
        .jobTitle("Gentleman of Fortune").build();
  }

  private UserEditRequest getUserEditRequest() {
    List<String> roles = new ArrayList<>();
    roles.add("ADMINISTRATOR");
    roles.add("TEACHER");
    roles.add("STUDENT");

    List<String> courses = new ArrayList<>();
    courses.add("Sailing");
    courses.add("Fencing");

    return UserEditRequest.builder()
        .id("id")
        .login("login")
        .password("password")
        .email("email")
        .roles(roles)
        .firstName("John")
        .lastName("Silver")
        .groupName("the Hispaniola")
        .jobTitle("Gentleman of Fortune").build();
  }

  private User getUser() {
    User user = getBasicUser();
    Set<Role> roles = new HashSet<>();
    roles.add(Role.builder().name("ADMINISTRATOR").build());
    roles.add(Role.builder().name("TEACHER").build());
    roles.add(Role.builder().name("STUDENT").build());
    user.setRoles(roles);

    List<UserDetails> userDetailsList = new ArrayList<>();
    userDetailsList.add(AdminDetails.builder()
        .firstName("John")
        .lastName("Silver")
        .jobTitle("Gentleman of Fortune").build());
    userDetailsList.add(TeacherDetails.builder()
        .firstName("John")
        .lastName("Silver").build());
    userDetailsList.add(StudentDetails.builder()
        .firstName("John")
        .lastName("Silver")
        .group(Group.builder().name("the Hispaniola").build()).build());
    user.setUserDetailsList(userDetailsList);

    return user;
  }

  private User getBasicUser() {
    return User.builder()
        .login("login")
        .password("password")
        .email("email").build();
  }

  private User getEmptyUser() {
    return User.builder().build();
  }

  private List<User> getUsers() {
    List<User> entities = new ArrayList<>();
    entities.add(User.builder().id("1").build());
    entities.add(User.builder().id("2").build());

    return entities;
  }
}
