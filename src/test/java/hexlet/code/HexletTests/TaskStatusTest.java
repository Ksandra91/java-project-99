package hexlet.code.HexletTests;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.context.WebApplicationContext;

import hexlet.code.HexletTests.model.TaskStatus;
import hexlet.code.HexletTests.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

import hexlet.code.AppApplication;
import hexlet.code.HexletTests.util.TestUtils;

@Order(3)
@ContextConfiguration(classes = AppApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskStatusTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    private ModelGenerator modelGenerator = new ModelGenerator();

    private TaskStatus testTaskStatus;


    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
    }

    @Order(1)
    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();

        var request = post("/api/task_statuses").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("name").isEqualTo(data.getName()),
                v -> v.node("slug").isEqualTo(data.getSlug()));
    }

    @Order(2)
    @Test
    public void testIndex() throws Exception {
        TestUtils.saveTaskStatus(mockMvc, testTaskStatus);

        var result = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }


    @Order(3)
    @Test
    public void testShow() throws Exception {
        TestUtils.saveTaskStatus(mockMvc, testTaskStatus);
        var status = TestUtils.getStatusByName(mockMvc, testTaskStatus.getName());

        var request = get("/api/task_statuses/{id}", status.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug()),
                v -> v.node("id").isPresent(),
                v -> v.node("createdAt").isPresent()
        );
    }

    @Order(4)
    @Test
    public void testUpdate() throws Exception {
        TestUtils.saveTaskStatus(mockMvc, testTaskStatus);
        var status = TestUtils.getStatusByName(mockMvc, testTaskStatus.getName());

        var data = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();

        var request = put("/api/task_statuses/{id}", status.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(data.getName()),
                v -> v.node("slug").isEqualTo(data.getSlug())
        );

        var actualStatus = TestUtils.getStatusByName(mockMvc, data.getName());
        assertEquals(data.getName(), actualStatus.getName());
        assertEquals(data.getSlug(), actualStatus.getSlug());
    }

    @Order(5)
    @Test
    public void testPartialUpdate() throws Exception {
        TestUtils.saveTaskStatus(mockMvc, testTaskStatus);
        var status = TestUtils.getStatusByName(mockMvc, testTaskStatus.getName());

        var data = new HashMap<String, String>();
        data.put("name", "new_name");

        var request = put("/api/task_statuses/{id}", status.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(data.get("name")),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );

        var actualStatus = TestUtils.getStatusByName(mockMvc, data.get("name"));
        assertEquals(data.get("name"), actualStatus.getName());
        assertEquals(testTaskStatus.getSlug(), actualStatus.getSlug());
    }

    @Order(6)
    @Test
    public void testDelete() throws Exception {
        TestUtils.saveTaskStatus(mockMvc, testTaskStatus);
        var status = TestUtils.getStatusByName(mockMvc, testTaskStatus.getName());

        mockMvc.perform(delete("/api/task_statuses/{id}", status.getId()).with(jwt()))
                .andExpect(status().isNoContent());
        var actualStatus = TestUtils.getStatusByName(mockMvc, testTaskStatus.getName());
        assertNull(actualStatus);
    }
}
