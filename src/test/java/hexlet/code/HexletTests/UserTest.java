package hexlet.code.HexletTests;

import java.nio.charset.StandardCharsets;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.Order;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.web.context.WebApplicationContext;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;


import hexlet.code.HexletTests.util.ModelGenerator;

import hexlet.code.AppApplication;
import hexlet.code.HexletTests.util.TestUtils;
import hexlet.code.HexletTests.model.User;


@Order(1)
@ContextConfiguration(classes = AppApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    private ModelGenerator modelGenerator = new ModelGenerator();

    private User testUser;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Order(1)
    @Test
    void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel())
                .create();

        var request = post("/api/users")
                .with(token)
                .contentType(APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("password").isAbsent(),
                v -> v.node("id").isPresent(),
                v -> v.node("firstName").isEqualTo(data.getFirstName()),
                v -> v.node("lastName").isEqualTo(data.getLastName()),
                v -> v.node("email").isEqualTo(data.getEmail()),
                v -> v.node("createdAt").isPresent()
        );
    }

    @Order(2)
    @Test
    public void testIndex() throws Exception {
        TestUtils.saveUser(mockMvc, testUser);
        var result = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Order(3)
    @Test
    public void testShow() throws Exception {
        TestUtils.saveUser(mockMvc, testUser);
        var user = TestUtils.getUserByEmail(mockMvc, testUser.getEmail());
        var request = get("/api/users/{id}", user.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("createdAt").isPresent(),
                v -> v.node("password").isAbsent()
        );
    }

    @Order(4)
    @Test
    public void testUpdate() throws Exception {
        TestUtils.saveUser(mockMvc, testUser);
        var user = TestUtils.getUserByEmail(mockMvc, testUser.getEmail());
        var data = Instancio.of(modelGenerator.getUserModel())
                .create();

        var request = put("/api/users/{id}", user.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(data.getEmail()),
                v -> v.node("firstName").isEqualTo(data.getFirstName()),
                v -> v.node("lastName").isEqualTo(data.getLastName())
        );

        var actualUser = TestUtils.getUserByEmail(mockMvc, data.getEmail());

        assertEquals(data.getEmail(), actualUser.getEmail());
        assertEquals(data.getFirstName(), actualUser.getFirstName());
        assertEquals(data.getLastName(), actualUser.getLastName());
    }

    @Order(5)
    @Test
    public void testPartialUpdate() throws Exception {
        TestUtils.saveUser(mockMvc, testUser);
        var user = TestUtils.getUserByEmail(mockMvc, testUser.getEmail());
        var data = new HashMap<String, String>();
        data.put("firstName", "New name");

        var request = put("/api/users/{id}", user.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(data.get("firstName")),
                v -> v.node("lastName").isEqualTo(testUser.getLastName())
        );

        var actualUser = TestUtils.getUserByEmail(mockMvc, testUser.getEmail());

        assertEquals(data.get("firstName"), actualUser.getFirstName());
        assertEquals(testUser.getLastName(), actualUser.getLastName());
        assertEquals(testUser.getEmail(), actualUser.getEmail());
    }

    @Order(6)
    @Test
    public void testDelete() throws Exception {
        TestUtils.saveUser(mockMvc, testUser);
        var user = TestUtils.getUserByEmail(mockMvc, testUser.getEmail());
        mockMvc.perform(delete("/api/users/{id}", user.getId()).with(token))
                .andExpect(status().isNoContent());
        var actualUser = TestUtils.getUserByEmail(mockMvc, testUser.getEmail());
        assertNull(actualUser);
    }
}
