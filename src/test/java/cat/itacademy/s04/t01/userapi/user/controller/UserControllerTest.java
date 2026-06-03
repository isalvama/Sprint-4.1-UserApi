package cat.itacademy.s04.t01.userapi.user.controller;
import cat.itacademy.s04.t01.userapi.user.dto.CreateUserDto;
import cat.itacademy.s04.t01.userapi.user.dto.UserResponse;
import cat.itacademy.s04.t01.userapi.user.exception.UserAlreadyExistsException;
import cat.itacademy.s04.t01.userapi.user.exception.UserNotFoundException;
import cat.itacademy.s04.t01.userapi.user.service.UserMapper;
import cat.itacademy.s04.t01.userapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String NAME = "Alice";
    private static final String EMAIL = "alice@mail.com";
    private static final String ID = UUID.randomUUID().toString();
    private static final UserResponse USER_RESPONSE = new UserResponse(NAME, EMAIL, ID);
    private UserMapper userMapper;
    private ObjectMapper objectMapper;


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;


    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("PUT /api/users")
    class CreateUser {
        @Test
        @DisplayName("returns 201 Created with Location header and response body")
        void createUser_returns201WithLocationAndBodyUserWithId() throws Exception {
            CreateUserDto createUserDto = new CreateUserDto(NAME, EMAIL);

            when(userService.createUser(createUserDto)).thenReturn(USER_RESPONSE);

            ResultActions result = mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createUserDto)));

            result.andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/users/" + ID)))
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.name").value(NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL));
            verify(userService).createUser(createUserDto);
        }

        @Test
        @DisplayName("returns 409 User Already Exists when email already exists")
        void createUser_returns409UserAlreadyExists() throws Exception {
            CreateUserDto createUserDto = new CreateUserDto(NAME, EMAIL);

            when(userService.createUser(createUserDto)).thenThrow(new UserAlreadyExistsException(createUserDto.email()));

            ResultActions result = mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createUserDto)));

            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("User Already Exists"))
                    .andExpect(jsonPath("$.status").value(409));
            verify(userService).createUser(createUserDto);
        }


        @Test
        @DisplayName("returns 400 Bad Request when input data is invalid (name is blank)")
        void createUser_returns400ValidationErrorInInputDataBlankName() throws Exception {
            String jsonInput =
                    "{\"name\": \"\", \"email\" : \"bob@domain.com\"}";


            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput));

           result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.title").value("Validation Error in input data"))
                            .andExpect(jsonPath("$.errors").exists());

           verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("returns 400 Bad Request when input data is invalid (email is blank)")
        void createUser_returns400ValidationErrorInInputDataBlankEmail() throws Exception {
            String jsonInput =
                    "{\"name\": \"Alice\", \"email\" : \"\"}";

            ResultActions result1 = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput));

            result1.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.title").value("Validation Error in input data"))
                            .andExpect(jsonPath("$.errors").exists());

            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("returns 400 Bad Request when input data is invalid (email is invalid)")
        void createUser_returns400ValidationErrorInInputDataInvalidEmail() throws Exception {
            String jsonInput =
                    "{\"name\": \"Alice\", \"email\" : \"invalidEmail\"}";

            ResultActions result1 = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput));

            result1.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Error in input data"))
                    .andExpect(jsonPath("$.errors").exists());
            verifyNoInteractions(userService);
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetUsers {

        @Test
        @DisplayName("returns 200 OK with empty list")
        void getUsers_returns200WithEmptyListInitially() throws Exception {
            ResultActions result = mockMvc.perform(get("/api/users"));
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
            verify(userService).getAllUsers();
        }

        @Test
        @DisplayName("returns 200 OK with list of users")
        void getUsers_returns200WithListOfUsers() throws Exception {
            String name2 = "Juan";
            String email2 = "juan@mail.com";
            String id2 = UUID.randomUUID().toString();
            UserResponse userResponse2 = new UserResponse(name2, email2, id2);

            when(userService.getAllUsers()).thenReturn(List.of(USER_RESPONSE, userResponse2));

            ResultActions result = mockMvc.perform(get("/api/users"));

            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(ID))
                    .andExpect(jsonPath("$[0].name").value(NAME))
                    .andExpect(jsonPath("$[0].email").value(EMAIL))
                    .andExpect(jsonPath("$[1].id").value(id2))
                    .andExpect(jsonPath("$[1].name").value(name2))
                    .andExpect(jsonPath("$[1].email").value(email2));
            verify(userService).getAllUsers();
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserById {

        @Test
        @DisplayName("returns 200 OK with user data")
        void getUserById_returns200WithUserData() throws Exception {
            when(userService.getUserById(ID))
                    .thenReturn(USER_RESPONSE);

            ResultActions resultGet = mockMvc.perform(get("/api/users/{id}", ID)
                    .contentType(MediaType.APPLICATION_JSON));

            resultGet.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.name").value(NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL));
            verify(userService).getUserById(any());
        }

        @Test
        @DisplayName("returns 404 when user is not found")
        void getUserById_returns404NotFoundIfMissing() throws Exception {

            when(userService.getUserById(ID)).thenThrow(new UserNotFoundException("id", ID));

            ResultActions resultGet = mockMvc.perform(get("/api/users/{id}", ID));

            resultGet.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("User Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
            verify(userService).getUserById(any());
        }

        @Test
        @DisplayName("returns 400 Bad Request when the path variable id is not of UUID type")
        void getUserById_idIsNotUUIDType_returns404ValidationErrorInParameter() throws Exception {
            String invalidId = "a".repeat(36);
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", invalidId)
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Error in Parameter"))
                    .andExpect(jsonPath("$.errors").exists());
            verifyNoInteractions(userService);
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetUsersByName {

        @Test
        @DisplayName("returns 200 OK with list of user data")
        void getUserByName_returnsListOfUsers() throws Exception {

            String email2 = "a@mail.com";
            String id2 = UUID.randomUUID().toString();
            UserResponse userResponse2 = new UserResponse(NAME, email2, id2);

            when(userService.getUserByName(NAME)).thenReturn(List.of(USER_RESPONSE, userResponse2));

            ResultActions result = mockMvc.perform(get("/api/users").param("name", NAME));
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(ID))
                    .andExpect(jsonPath("$[0].name").value(NAME))
                    .andExpect(jsonPath("$[0].email").value(EMAIL))
                    .andExpect(jsonPath("$[1].id").value(id2))
                    .andExpect(jsonPath("$[1].name").value(NAME))
                    .andExpect(jsonPath("$[1].email").value(email2));
            verify(userService).getUserByName(NAME);
        }

        @Test
        @DisplayName("returns 404 when users with name passed as path variable are not found")
        void getUserByName_returns404NotFoundIfMissing() throws Exception {

            when(userService.getUserByName(NAME)).thenThrow(new UserNotFoundException("name", NAME));

            ResultActions result = mockMvc.perform(get("/api/users").param("name", NAME));
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("User Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
            verify(userService).getUserByName(NAME);
        }

        @Test
        @DisplayName("returns 200 OK when path variable is empty, blank or null and calls userServiceImpl.getAllUsers() instead")
        void getUserByName_nameIsInvalid_returns200AnCallsGetAllUsers() throws Exception {
            ResultActions result1 = mockMvc.perform(get("/api/users").param("name", " "));
            ResultActions result2 = mockMvc.perform(get("/api/users"));
            ResultActions result3 = mockMvc.perform(get("/api/users").param("name", ""));

            result1.andExpect(status().isOk());
            result2.andExpect(status().isOk());
            result3.andExpect(status().isOk());

            verify(userService, never()).getUserByName(anyString());
            verify(userService, times(3)).getAllUsers();
        }
    }
}