package hexlet.code;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import hexlet.code.model.User;
import net.datafaker.Faker;
import hexlet.code.util.ModelGenerator;
import hexlet.code.dto.User.UserUpdateDTO;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskStatusRepository statusRepository;

    // Нужно создать бин
    @Autowired
    private Faker faker;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;


    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
    }

    @AfterEach
    public void clean() {
        taskRepository.deleteAll();
        statusRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/" + testUser.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("username").isEqualTo(testUser.getEmail())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel())
                .create();

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.getEmail()).get();

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
    }

    @Test
    public void testUpdate() throws Exception {
        var testUser2 = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser2);
        var token2 = jwt().jwt(builder -> builder.subject(testUser2.getEmail()));
        var data = new HashMap<>();
        data.put("firstName", "first");

        var request = put("/api/users/" + testUser2.getId())
                .with(token2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var user = userRepository.findById(testUser2.getId()).get();
        assertThat(user.getFirstName()).isEqualTo(("first"));
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        var request = get("/api/users/" + testUser.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateAnotherUser() throws Exception {
        var user = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(user);

        var newFirstName = "updated firstname";
        var updatedDTO = new UserUpdateDTO();
        updatedDTO.setFirstName(JsonNullable.of(newFirstName));

        var request = put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updatedDTO))
                .with(token);

        mockMvc.perform(request).andExpect(status().isForbidden());

        var userFromRepo = userRepository.findByEmail(user.getEmail()).get();

        assertThat(userFromRepo).isNotNull();
        assertThat(userFromRepo.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(userFromRepo.getLastName()).isEqualTo(user.getLastName());
        assertThat(userFromRepo.getEmail()).isEqualTo(user.getEmail());

    }

    @Test
    public void testDestroyAnotherUser() throws Exception {
        var user = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(user);

        var request = delete("/api/users/{id}", user.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isForbidden());

        Assertions.assertThat(userRepository.existsById(user.getId())).isTrue();
    }
}
