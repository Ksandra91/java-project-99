package hexlet.code.HexletTests;


import java.nio.charset.StandardCharsets;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.web.context.WebApplicationContext;


import org.instancio.Instancio;
import org.instancio.Select;
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

import java.util.Set;
import java.util.HashMap;

import hexlet.code.AppApplication;
import hexlet.code.HexletTests.util.ModelGenerator;
import hexlet.code.HexletTests.util.TestUtils;

import hexlet.code.HexletTests.model.Task;


@Order(4)
@ContextConfiguration(classes = AppApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    private ModelGenerator modelGenerator = new ModelGenerator();

    private Task testTask;

    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        var taskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        TestUtils.saveTaskStatus(mockMvc, taskStatus);

        var labelData = Instancio.of(modelGenerator.getLabelModel()).create();
        TestUtils.saveLabel(mockMvc, labelData);
        var label = TestUtils.getLabelByName(mockMvc, labelData.getName());

        testTask = Instancio.of(modelGenerator.getTaskModel())
                .set(Select.field(Task::getAssignee), null)
                .create();
        testTask.setTaskStatus(taskStatus.getSlug());
        testTask.setLabels(Set.of(label.getId()));
    }

    @Order(1)
    @Test
    public void testCreate() throws Exception {
        var name = "Task Name";
        var content = "Task Content";
        var data = new HashMap<String, Object>();
        data.put("title", name);
        data.put("content", content);
        data.put("status", testTask.getTaskStatus());
        data.put("taskLabelIds", testTask.getLabels());
        var request = post("/api/tasks").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("content").isPresent(),
                v -> v.node("title").isPresent(),
                v -> v.node("status").isEqualTo(data.get("status")),
                v -> v.node("taskLabelIds").isEqualTo(testTask.getLabels())
        );
    }

    @Order(2)
    @Test
    public void testIndex() throws Exception {
        TestUtils.saveTask(mockMvc, testTask);
        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Order(3)
    @Test
    public void testShow() throws Exception {
        TestUtils.saveTask(mockMvc, testTask);
        var task = TestUtils.getTaskByName(mockMvc, testTask.getName());
        var request = get("/api/tasks/{id}", task.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus()),
                v -> v.node("taskLabelIds").isEqualTo(testTask.getLabels())
        );
    }

    @Order(4)
    @Test
    public void testUpdate() throws Exception {
        TestUtils.saveTask(mockMvc, testTask);
        var task = TestUtils.getTaskByName(mockMvc, testTask.getName());
        var data = new HashMap<String, String>();
        var name = "New Task Name";
        data.put("title", name);

        var request = put("/api/tasks/{id}", task.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("title").isEqualTo(data.get("title")),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus()),
                v -> v.node("taskLabelIds").isEqualTo(testTask.getLabels())
        );

        var actualTask = TestUtils.getTaskByName(mockMvc, name);

        assertEquals(name, actualTask.getName());
        assertEquals(testTask.getDescription(), actualTask.getDescription());
        assertEquals(testTask.getTaskStatus(), actualTask.getTaskStatus());
        assertEquals(testTask.getLabels(), actualTask.getLabels());
    }

    @Order(5)
    @Test
    public void testDelete() throws Exception {
        TestUtils.saveTask(mockMvc, testTask);
        var task = TestUtils.getTaskByName(mockMvc, testTask.getName());

        mockMvc.perform(delete("/api/tasks/{id}", task.getId()).with(jwt()))
                .andExpect(status().isNoContent());

        var actualTask = TestUtils.getTaskByName(mockMvc, testTask.getName());
        assertNull(actualTask);
    }
}

