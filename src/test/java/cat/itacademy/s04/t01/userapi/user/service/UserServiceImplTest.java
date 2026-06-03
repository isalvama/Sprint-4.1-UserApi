package cat.itacademy.s04.t01.userapi.user.service;

import cat.itacademy.s04.t01.userapi.user.dto.CreateUserDto;
import cat.itacademy.s04.t01.userapi.user.dto.UserResponse;
import cat.itacademy.s04.t01.userapi.user.exception.UserAlreadyExistsException;
import cat.itacademy.s04.t01.userapi.user.exception.UserNotFoundException;
import cat.itacademy.s04.t01.userapi.user.model.User;
import cat.itacademy.s04.t01.userapi.user.repository.InMemoryUserRepository;
import cat.itacademy.s04.t01.userapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private static final String NAME = "Alice";
    private static final String EMAIL = "alice@mail.com";
    private static final UUID ID = UUID.randomUUID();
    private static final CreateUserDto createUserDto = new CreateUserDto(NAME, EMAIL);
    private static final User user = new User(NAME, EMAIL, ID);
    private UserRepository userRepository;
    private UserService userService;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(InMemoryUserRepository.class);
        userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Nested
    @DisplayName("createUser()")
    class createUser{
        @Test
        @DisplayName("createUser() creates a new User successfully returning UserResponse with the user's data and a generated UUID id")
        void createsUser_whenUserWithMatchingEmailDoesNotExist() {
            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

            UserResponse response = userService.createUser(createUserDto);

            assertNotNull(response);
            assertNotNull(response.id());
            assertEquals(NAME, response.name());
            assertEquals(EMAIL, response.email());

            verify(userRepository, times(1)).existsByEmail(EMAIL);
            verify(userRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("createUser() throws UserAlreadyExistsException when user's email already exists in db")
        void throwsUserAlreadyExistsException_whenUserWithMatchingAlreadyExists() {
            when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

            Exception exception = assertThrows(UserAlreadyExistsException.class, ()-> {userService.createUser(createUserDto);});
            assertEquals("User already exists with email: " + EMAIL, exception.getMessage());

            verify(userRepository, times(1)).existsByEmail(EMAIL);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("getAllUsers()")
    class getAllUsers{
        @Test
        @DisplayName("getAllUsers() returns a list of users mapped to List<UserResponse>")
        void shouldReturnListOfUserResponse_whenUsersExistInDb() {
            when(userRepository.findAll()).thenReturn(List.of(user));

            List<UserResponse> response = userService.getAllUsers();

            assertNotNull(response);
            assertEquals(1, response.size());
            assertEquals(NAME, response.getFirst().name());
            assertEquals(EMAIL, response.getFirst().email());
            assertEquals(ID.toString(), response.getFirst().id());

            verify(userRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("getAllUsers() returns an empty list of UserResponse")
        void shouldReturnEmptyListOfUserResponse_whenUsersDoNotExistInDb() {
            when(userRepository.findAll()).thenReturn(List.of());

            List<UserResponse> response = userService.getAllUsers();

            assertNotNull(response);
            assertEquals(0, response.size());

            verify(userRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getUserById()")
    class getUserById{
        @Test
        @DisplayName("getUserById() returns a UserResponse instance with user's data when a user with matching id exists in db")
        void shouldReturnUserResponse_whenUserWithMatchingIdExistsInDb() {
            when(userRepository.findById(ID)).thenReturn(Optional.of(user));

            UserResponse response = userService.getUserById(ID.toString());

            assertNotNull(response);
            assertEquals(NAME, response.name());
            assertEquals(EMAIL, response.email());
            assertEquals(ID.toString(), response.id());

            verify(userRepository, times(1)).findById(ID);
        }

        @Test
        @DisplayName("getUserById() throws UserNotFoundException when a user with matching id does not exist in db")
        void shouldThrowUserNotFoundException_whenUserDoesNotExistInDbWithMatchingId() {
            when(userRepository.findById(any())).thenReturn(Optional.empty());

            Exception exception = assertThrows(UserNotFoundException.class, ()->{userService.getUserById(String.valueOf(ID));});
            assertEquals("User not found with id: " + ID, exception.getMessage());

            verify(userRepository, times(1)).findById(ID);
        }
    }

    @Nested
    @DisplayName("getUserByName()")
    class getUserByName{
        @Test
        @DisplayName("getUserByName() returns a List<UserResponse> instance with multiple user's data when users with the name passed as parameter exist in db")
        void shouldReturnListOfUserResponse_whenUsersWithNameExistInDb() {
            when(userRepository.searchByName(NAME)).thenReturn(List.of(user));

            List<UserResponse> response = userService.getUserByName(NAME);

            assertNotNull(response);
            assertEquals(NAME, response.getFirst().name());
            assertEquals(EMAIL, response.getFirst().email());
            assertEquals(ID.toString(), response.getFirst().id());

            verify(userRepository, times(1)).searchByName(NAME);
        }

        @Test
        @DisplayName("getUserByName() throws UserNotFoundException when a user with matching id does not exist in db")
        void shouldThrowUserNotFoundException_whenUsersWithNameDoNotExistsInDb() {
            when(userRepository.searchByName(any())).thenReturn(List.of());

            Exception exception = assertThrows(UserNotFoundException.class, ()->{userService.getUserByName(NAME);});
            assertEquals("User not found with name: " + NAME, exception.getMessage());

            verify(userRepository, times(1)).searchByName(NAME);
        }
    }
}