package ua.foxminded.universitycms.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ua.foxminded.universitycms.domain.Course;
import ua.foxminded.universitycms.domain.Group;
import ua.foxminded.universitycms.domain.Role;
import ua.foxminded.universitycms.domain.User;
import ua.foxminded.universitycms.domain.universityclass.UniversityClass;
import ua.foxminded.universitycms.domain.userdetails.AdminDetails;
import ua.foxminded.universitycms.domain.userdetails.StudentDetails;
import ua.foxminded.universitycms.domain.userdetails.TeacherDetails;
import ua.foxminded.universitycms.domain.userdetails.UserDetails;
import ua.foxminded.universitycms.dto.UserCreateRequest;
import ua.foxminded.universitycms.dto.UserEditRequest;
import ua.foxminded.universitycms.dto.UserResponse;

class UserMapperTest {
  UserMapper mapper = new UserMapperImpl();
  
  @Test
  void userToUserResponseShouldReturnNullIfUserIsNull() {
    User user = null;
    assertThat(mapper.userToUserResponse(user)).isEqualTo(null);
  }
  
  @Test
  void userToUserResponseShouldReturnExpectedResponseIfUserIsAdmin() {
    User user = getUser();
    
    Set<Role> roles = new HashSet<>();
    roles.add(Role.builder().name("administrator").build());
    roles.add(null);
    user.setRoles(roles);
    
    List<UserDetails> userDetailsList = new ArrayList<>();
    userDetailsList.add(AdminDetails.builder()
        .firstName("Admin")
        .lastName("Adminson")
        .jobTitle("admin").build());
    user.setUserDetailsList(userDetailsList);
    
    List<String> dtoRoles = new ArrayList<>();
    dtoRoles.add("administrator");
    dtoRoles.add(null);
    UserResponse expectedDto = UserResponse.builder()
        .id("id")
        .login("login")
        .email("email")
        .roles(dtoRoles)
        .firstName("Admin")
        .lastName("Adminson")
        .jobTitle("admin")
        .groupName(null).build();
        
    assertThat(mapper.userToUserResponse(user)).isEqualTo(expectedDto);
  }
  
  @Test
  void userToUserResponseShouldReturnExpectedResponseIfUserIsTeacher() {
    User user = getUser();
    
    Set<Role> roles = new HashSet<>();
    roles.add(Role.builder().name("teacher").build());
    user.setRoles(roles);
    
    List<UserDetails> userDetailsList = new ArrayList<>();
    List<Course> courses = new ArrayList<>();
    courses.add(Course.builder().name("course").build());
    userDetailsList.add(TeacherDetails.builder()
        .firstName("Teach")
        .lastName("Lessons").build());
    user.setUserDetailsList(userDetailsList);
    
    List<String> dtoRoles = new ArrayList<>();
    dtoRoles.add("teacher");
    List<String> dtoCourses = new ArrayList<>();
    dtoCourses.add("course");
    UserResponse expectedDto = UserResponse.builder()
        .id("id")
        .login("login")
        .email("email")
        .roles(dtoRoles)
        .firstName("Teach")
        .lastName("Lessons")
        .jobTitle(null)
        .groupName(null).build();
        
    assertThat(mapper.userToUserResponse(user)).isEqualTo(expectedDto);
  }
  
  @Test
  void userToUserResponseShouldReturnExpectedResponseIfUserIsStudent() {
    User user = getUser();
    
    Set<Role> roles = new HashSet<>();
    roles.add(Role.builder().name("student").build());
    user.setRoles(roles);
    
    List<UserDetails> userDetailsList = new ArrayList<>();
    userDetailsList.add(StudentDetails.builder()
        .firstName("Student")
        .lastName("Sleepings")
        .group(Group.builder().name("group").build()).build());
    user.setUserDetailsList(userDetailsList);
    
    List<String> dtoRoles = new ArrayList<>();
    dtoRoles.add("student");
    UserResponse expectedDto = UserResponse.builder()
        .id("id")
        .login("login")
        .email("email")
        .roles(dtoRoles)
        .firstName("Student")
        .lastName("Sleepings")
        .jobTitle(null)
        .groupName("group").build();
        
    assertThat(mapper.userToUserResponse(user)).isEqualTo(expectedDto);
  }
  
  @Test
  void userToUserResponseShouldReturnExpectedResponseIfUserIsStudentWithoutGroup() {
    User user = getUser();
    
    Set<Role> roles = new HashSet<>();
    roles.add(Role.builder().name("student").build());
    user.setRoles(roles);
    
    List<UserDetails> userDetailsList = new ArrayList<>();
    userDetailsList.add(StudentDetails.builder()
        .firstName("Student")
        .lastName("Sleepings").build());
    user.setUserDetailsList(userDetailsList);
    
    List<String> dtoRoles = new ArrayList<>();
    dtoRoles.add("student");
    UserResponse expectedDto = UserResponse.builder()
        .id("id")
        .login("login")
        .email("email")
        .roles(dtoRoles)
        .firstName("Student")
        .lastName("Sleepings")
        .jobTitle(null)
        .groupName(null).build();
        
    assertThat(mapper.userToUserResponse(user)).isEqualTo(expectedDto);
  }
  
  @Test
  void userToUserResponseShouldReturnExpectedResponseIfUserDetailsIsNull() {
    User user = getUser();
    
    UserResponse expectedDto = UserResponse.builder()
        .id("id")
        .login("login")
        .email("email")
        .roles(null)
        .firstName(null)
        .lastName(null)
        .jobTitle(null)
        .groupName(null).build();
    
    assertThat(mapper.userToUserResponse(user)).isEqualTo(expectedDto);
  }
  
  @Test
  void userToUserResponseShouldReturnExpectedResponseIfUserDetailsIsEmpty() {
    User user = getUser();
    user.setUserDetailsList(new ArrayList<UserDetails>());
    
    UserResponse expectedDto = UserResponse.builder()
        .id("id")
        .login("login")
        .email("email")
        .roles(null)
        .firstName(null)
        .lastName(null)
        .jobTitle(null)
        .groupName(null).build();
    
    assertThat(mapper.userToUserResponse(user)).isEqualTo(expectedDto);
  }
  
  @Test
  void usersToUserResponsesShouldReturnNullIfUsersIsNull() {
    List<User> users = null;
    assertThat(mapper.usersToUserResponses(users)).isEqualTo(null);
  }
  
  @Test
  void usersToUserResponsesShouldMapMultimpleUsers() {
    List<User> users = new ArrayList<>();
    users.add(getUser());
    users.add(getUser());
    
    List<UserResponse> expectedDtos = new ArrayList<>();
    expectedDtos.add(UserResponse.builder()
        .id("id")
        .login("login")
        .email("email").build());
    expectedDtos.add(UserResponse.builder()
        .id("id")
        .login("login")
        .email("email").build());
    
    assertThat(mapper.usersToUserResponses(users)).isEqualTo(expectedDtos);
  }
  
  @Test
  void userToUserEditRequestShouldReturnNullIfUserIsNull() {
    User user = null;
    assertThat(mapper.userToUserEditRequest(user)).isEqualTo(null);
  }
  
  @Test
  void userToUserEditRequestShouldReturnExpectedUserEditRequest() {
    User user = getUser();
    
    Set<Role> roles = new HashSet<>();
    roles.add(Role.builder().name("administrator").build());
    user.setRoles(roles);
    
    List<UserDetails> userDetailsList = new ArrayList<>();
    userDetailsList.add(AdminDetails.builder()
        .firstName("Admin")
        .lastName("Adminson")
        .jobTitle("admin").build());
    user.setUserDetailsList(userDetailsList);
    
    List<String> dtoRoles = new ArrayList<>();
    dtoRoles.add("administrator");
    UserEditRequest expectedDto = UserEditRequest.builder()
        .id("id")
        .login("login")
        .password("password")
        .email("email")
        .roles(dtoRoles)
        .firstName("Admin")
        .lastName("Adminson")
        .jobTitle("admin")
        .groupName(null).build();
        
    assertThat(mapper.userToUserEditRequest(user)).isEqualTo(expectedDto);
  }
  
  @Test
  void userCreateRequestToUserShouldReturnNullIfUserCreateRequestIsNull() {
    UserCreateRequest user = null;
    assertThat(mapper.userCreateRequestToUser(user)).isEqualTo(null);
  }
  
  @Test
  void userEditRequestToUserShouldReturnNullIfUserEditRequestIsNull() {
    UserEditRequest user = null;
    assertThat(mapper.userEditRequestToUser(user)).isEqualTo(null);
  }
  
  @Test
  void userCreateRequestToUserShouldReturnExpectedUser() {
    List<String> roleNames = new ArrayList<>();
    roleNames.add("Administrator");
    roleNames.add("Teacher");
    roleNames.add("Student");
    
    UserCreateRequest dto = UserCreateRequest.builder()
        .login("login")
        .password("password")
        .email("email")
        .roles(roleNames)
        .firstName("firstName")
        .lastName("lastName")
        .jobTitle("job")
        .groupName("group").build();
    
    User expectedUser = getUser();
    expectedUser.setId(null);
    
    assertThat(mapper.userCreateRequestToUser(dto)).isEqualTo(expectedUser);
  }
  
  @Test
  void userEditRequestToUserShouldReturnExpectedUser() {
    List<UniversityClass> classes = new ArrayList<>();
    classes.add(UniversityClass.builder().build());
    
    List<String> roleNames = new ArrayList<>();
    roleNames.add("Administrator");
    roleNames.add("Teacher");
    roleNames.add("Student");
    
    UserEditRequest dto = UserEditRequest.builder()
        .id("id")
        .login("login")
        .password("password")
        .email("email")
        .roles(roleNames)
        .firstName("firstName")
        .lastName("lastName")
        .jobTitle("job")
        .groupName("group").build();
    
    User expectedUser = getUser();
    
    assertThat(mapper.userEditRequestToUser(dto)).isEqualTo(expectedUser);
  }
  
  private User getUser() {
    return User.builder()
        .id("id")
        .login("login")
        .password("password")
        .email("email").build();
  }
}
