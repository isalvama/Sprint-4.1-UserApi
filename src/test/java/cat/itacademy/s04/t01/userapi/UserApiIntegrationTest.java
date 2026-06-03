package cat.itacademy.s04.t01.userapi;

import cat.itacademy.s04.t01.userapi.user.dto.CreateUserDto;
import cat.itacademy.s04.t01.userapi.user.model.User;
import cat.itacademy.s04.t01.userapi.user.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = UserapiApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserApiIntegrationTest {
    private static final String NAME = "David";
    private static final String EMAIL = "d@mail.com";
    private static final UUID ID = UUID.randomUUID();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    InMemoryUserRepository userRepository;

    @BeforeEach
    void cleanUp(){
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("PUT /api/users")
    class CreateUser {
        @Test
        void createUser_returns201WithLocationWithId() throws Exception {

            CreateUserDto createFruitDTO = new CreateUserDto(NAME, EMAIL);

            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createFruitDTO)));

            result.andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/users/")))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value(NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL));
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetUsers {
        @BeforeEach
        void init() {
            userRepository.save(new User(NAME, EMAIL, UUID.randomUUID()));
            userRepository.save(new User("Daniel", "daniel@mail.com", UUID.randomUUID()));
            userRepository.save(new User("Monica", "m@mail.com", UUID.randomUUID()));
        }

        @Test
        void getUsers_returns200WithListOfSize3() throws Exception {
            ResultActions result = mockMvc.perform(get("/api/users"));
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(3)));
        }

        @Test
        void getUsersWithNameParam_returns200WithUsersContainingStringInTheirName() throws Exception {
            ResultActions result = mockMvc.perform(get("/api/users").param("name", "da"));
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value(NAME))
                    .andExpect(jsonPath("$[1].name").value("Daniel"));
        }

        @Test
        void getUsersWithNameParam_returns404UserNotFound() throws Exception {
            ResultActions result = mockMvc.perform(get("/api/users").param("name", "sergi"));
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("User Not Found"))
                    .andExpect(jsonPath("$.detail", containsString("sergi")));
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetUserById {
        @BeforeEach
        void init() {
            userRepository.save(new User(NAME, EMAIL, ID));
            userRepository.save(new User("Daniel", "daniel@mail.com", UUID.randomUUID()));
            userRepository.save(new User("Monica", "m@mail.com", UUID.randomUUID()));
        }

        @Test
        void getUserById_returns200WithUserDataWithMatchingId() throws Exception {
            ResultActions result = mockMvc.perform(get("/api/users/{id}", ID));
            result.andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.id").value(ID.toString()));
        }

        @Test
        void getUserById_returns404NotFound() throws Exception {
            UUID id = UUID.randomUUID();
            ResultActions result = mockMvc.perform(get("/api/users/{id}", id));
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("User Not Found"))
                    .andExpect(jsonPath("$.detail", containsString(id.toString())));
        }
    }
}