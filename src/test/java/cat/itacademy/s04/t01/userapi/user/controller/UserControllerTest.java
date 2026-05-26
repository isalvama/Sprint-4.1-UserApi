package cat.itacademy.s04.t01.userapi.user.controller;
import cat.itacademy.s04.t01.userapi.user.dto.CreateUserDto;
import cat.itacademy.s04.t01.userapi.user.dto.UserResponse;
import cat.itacademy.s04.t01.userapi.user.exception.UserAlreadyExistsException;
import cat.itacademy.s04.t01.userapi.user.exception.UserNotFoundException;
import cat.itacademy.s04.t01.userapi.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String NAME = "Alice";
    private static final String EMAIL = "alice@mail.com";
    private static final String ID = UUID.randomUUID().toString();
    private static final UserResponse USER_RESPONSE = new UserResponse(NAME, EMAIL, ID);

    @MockitoBean
    private UserService userServiceImpl;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("PUT /user")
    class CreateUser {
        @Test
        @DisplayName("returns 201 Created with Location header and response body")
        void createUser_returns201WithLocationAndBodyUserWithId() throws Exception {
            CreateUserDto createUserDto = new CreateUserDto(NAME, EMAIL);

            when(userServiceImpl.createUser(createUserDto)).thenReturn(USER_RESPONSE);

            ResultActions result = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createUserDto)));

            result.andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/user/" + ID)))
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.name").value(NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL));
            verify(userServiceImpl).createUser(createUserDto);
        }

        @Test
        @DisplayName("returns 409 User Already Exists when email already exists")
        void createUser_returns409UserAlreadyExists() throws Exception {
            CreateUserDto createUserDto = new CreateUserDto(NAME, EMAIL);

            when(userServiceImpl.createUser(createUserDto)).thenThrow(new UserAlreadyExistsException(createUserDto.email()));

            ResultActions result = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createUserDto)));

            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("User Already Exists"))
                    .andExpect(jsonPath("$.status").value(409));
            verify(userServiceImpl).createUser(createUserDto);
        }


        @Test
        @DisplayName("returns 400 Bad Request when input data is invalid (name is blank)")
        void createUser_returns404ValidationErrorInInputDataBlankName() throws Exception {
            String jsonInput =
                    "{\"name\": \"\", \"email\" : \"bob@domain.com\"}";


            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput));

           result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.title").value("Validation Error in input data"))
                            .andExpect(jsonPath("$.errors").exists());

           verifyNoInteractions(userServiceImpl);
        }

        @Test
        @DisplayName("returns 400 Bad Request when input data is invalid (email is blank)")
        void createUser_returns404ValidationErrorInInputDataBlankEmail() throws Exception {
            String jsonInput =
                    "{\"name\": \"Alice\", \"email\" : \"\"}";

            ResultActions result1 = mockMvc.perform(MockMvcRequestBuilders.post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput));

            result1.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.title").value("Validation Error in input data"))
                            .andExpect(jsonPath("$.errors").exists());
            verifyNoInteractions(userServiceImpl);
        }

        @Test
        @DisplayName("returns 400 Bad Request when input data is invalid (name is null)")
        void createUser_returns404ValidationErrorInInputDataInvalidEmail() throws Exception {
            String jsonInput =
                    "{\"name\": \"Alice\", \"email\" : \"invalidEmail\"}";

            ResultActions result1 = mockMvc.perform(MockMvcRequestBuilders.post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput));

            result1.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Error in input data"))
                    .andExpect(jsonPath("$.errors").exists());
            verifyNoInteractions(userServiceImpl);
        }
    }

    @Nested
    @DisplayName("GET /users")
    class GetUsers {

        @Test
        @DisplayName("returns 200 OK with empty list")
        void getUsers_returns200WithEmptyListInitially() throws Exception {
            ResultActions result = mockMvc.perform(get("/users"));
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("returns 200 OK with list of users")
        void getUsers_returns200WithListOfUsers() throws Exception {
            String name2 = "Juan";
            String email2 = "juan@mail.com";
            String id2 = UUID.randomUUID().toString();
            UserResponse userResponse2 = new UserResponse(name2, email2, id2);

            when(userServiceImpl.getAllUsers()).thenReturn(List.of(USER_RESPONSE, userResponse2));

            ResultActions result = mockMvc.perform(get("/users"));

            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(ID))
                    .andExpect(jsonPath("$[0].name").value(NAME))
                    .andExpect(jsonPath("$[0].email").value(EMAIL))
                    .andExpect(jsonPath("$[1].id").value(id2))
                    .andExpect(jsonPath("$[1].name").value(name2))
                    .andExpect(jsonPath("$[1].email").value(email2));
        }
    }

    @Nested
    @DisplayName("GET /user/{id}")
    class GetUserById {

        @Test
        @DisplayName("returns 200 OK with user data")
        void getUserById_returns200WithUserData() throws Exception {
            when(userServiceImpl.getUserById(ID))
                    .thenReturn(USER_RESPONSE);

            ResultActions resultGet = mockMvc.perform(get("/user/{id}", ID)
                    .contentType(MediaType.APPLICATION_JSON));

            resultGet.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.name").value(NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL));
        }

        @Test
        @DisplayName("returns 404 when user not found")
        void getUserById_returns404NotFoundIfMissing() throws Exception {
            String randomId = UUID.randomUUID().toString();

            when(userServiceImpl.getUserById(randomId)).thenThrow(new UserNotFoundException("id", randomId));

            ResultActions resultGet = mockMvc.perform(get("/user/{id}", randomId));

            resultGet.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("User Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("returns 400 Bad Request when path variable id size is greater than max constraint")
        void getUserById_idIsGreaterThanMaxConstraint_returns404ValidationErrorInParameter() throws Exception {
            String invalidId = "a".repeat(41);
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", invalidId)
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Error in Parameter"))
                    .andExpect(jsonPath("$.errors").exists());
            verifyNoInteractions(userServiceImpl);
        }

        @Test
        @DisplayName("returns 400 Bad Request when path variable id size is smaller than min constraint")
        void getUserById_idIsGreaterThanMinConstraint_returns404ValidationErrorInParameter() throws Exception {
            String invalidId = "a".repeat(29);
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", invalidId)
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Error in Parameter"))
                    .andExpect(jsonPath("$.errors").exists());
            verifyNoInteractions(userServiceImpl);
        }
    }

    @Nested
    @DisplayName("GET /users/search/{name}")
    class GetUsersByName {

        @Test
        @DisplayName("returns 200 OK with list of user data")
        void getUserByName_returnsListOfUsers() throws Exception {

            String email2 = "a@mail.com";
            String id2 = UUID.randomUUID().toString();
            UserResponse userResponse2 = new UserResponse(NAME, email2, id2);

            when(userServiceImpl.getUserByName(NAME)).thenReturn(List.of(USER_RESPONSE, userResponse2));

            ResultActions result = mockMvc.perform(get("/users/search/{name}", NAME));
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(ID))
                    .andExpect(jsonPath("$[0].name").value(NAME))
                    .andExpect(jsonPath("$[0].email").value(EMAIL))
                    .andExpect(jsonPath("$[1].id").value(id2))
                    .andExpect(jsonPath("$[1].name").value(NAME))
                    .andExpect(jsonPath("$[1].email").value(email2));
        }

        @Test
        @DisplayName("returns 404 when users with name passed as path variable are not found")
        void getUserByName_returns404NotFoundIfMissing() throws Exception {

            when(userServiceImpl.getUserByName(NAME)).thenThrow(new UserNotFoundException("name", NAME));

            ResultActions result = mockMvc.perform(get("/users/search/{name}", NAME));
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("User Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("returns 400 Bad Request when path variable is empty")
        void getUserByName_nameIsEmpty_returns404ValidationErrorInParameter() throws Exception {
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/users/search/{name}", "")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Error in Parameter"))
                    .andExpect(jsonPath("$.errors").exists());
            verifyNoInteractions(userServiceImpl);
        }
    }
}