package cat.itacademy.s04.t01.userapi.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private UserController controller;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clear (){
        controller.users.clear();
    }

    @Test
    void getUsers_returnsEmptyListInitially() throws Exception {
        ResultActions result = mockMvc.perform(get("/users"));
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isEmpty());
    }

    @Test
    void createUser_returnsUserWithId() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("Alice", "alice@email.com");
        ResultActions result = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)));

        result.andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@email.com"))
                .andExpect(jsonPath("$.uuid").exists());
    }

    @Test
    void getUserById_returnsCorrectUser() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("Alice", "alice@email.com");
        MvcResult resultCreate = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto))).andReturn();

        String content = resultCreate.getResponse().getContentAsString();
        User createdUser = new ObjectMapper().readValue(content, User.class);
        ResultActions resultGet = mockMvc.perform(get("/users/" + createdUser.getUuid())
                .contentType(MediaType.APPLICATION_JSON));
        resultGet.andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@email.com"));
    }

    @Test
    void getUserById_returnsNotFoundIfMissing() throws Exception {

        ResultActions resultGet = mockMvc.perform(get("/users/" + "randomid")
                .contentType(MediaType.APPLICATION_JSON));
        resultGet.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getUsers_withNameParam_returnsFilteredUsers() throws Exception {

        CreateUserDto createUserDtoJuan = new CreateUserDto("Juan", "juan@mail.com");
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDtoJuan))).andReturn();

        CreateUserDto createUserDtoJoe = new CreateUserDto("Joe", "joe@mail.com");
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDtoJoe))).andReturn();



        ResultActions resultGet = mockMvc.perform(get("/users").param("name", "Juan").contentType(MediaType.APPLICATION_JSON));
        resultGet.andExpect(jsonPath("$[0].name").value("Juan"))
                .andExpect(jsonPath("$[0].email").value("juan@mail.com"));
    }

    @Test
    void getUserByName_withNamePathVariable_returnsFilteredUsers() throws Exception {

        CreateUserDto createUserDtoJuan = new CreateUserDto("Albert", "a@mail.com");
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDtoJuan))).andReturn();

        CreateUserDto createUserDtoJoe = new CreateUserDto("Maria", "m@mail.com");
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDtoJoe))).andReturn();

        ResultActions resultGet = mockMvc.perform(get("/users/search/" + "maria")
                .contentType(MediaType.APPLICATION_JSON));
        resultGet.andExpect(jsonPath("$.name").value("Maria"))
                .andExpect(jsonPath("$.email").value("m@mail.com"));
    }
}