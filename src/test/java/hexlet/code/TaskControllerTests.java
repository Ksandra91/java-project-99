package hexlet.code;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@AutoConfigureMockMvc

public class TaskControllerTests {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    private User testUser;
    private TaskStatus testStatus;
    private Label testLabel;
    private Task testTask;


    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);

        testStatus = new TaskStatus();
        testStatus.setName("TestStatus");
        testStatus.setSlug("forTest");
        statusRepository.save(testStatus);

        testLabel = new Label();
        testLabel.setName("testLabel");
        labelRepository.save(testLabel);

        testTask = new Task();
        testTask.setName("TaskName");
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testStatus);
        testTask.setLabels(List.of(testLabel));
        taskRepository.save(testTask);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
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
        var result = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/" + testTask.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getName())
        );

    }

    @Test
    public void testCreate() throws Exception {
        User testUser2 = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser2);

        TaskStatus testStatus2 = new TaskStatus();
        testStatus2.setName("TestStatus10");
        testStatus2.setSlug("forTest1111");
        statusRepository.save(testStatus2);

        Label testLabel2 = new Label();
        testLabel2.setName("testLabel1111111");
        labelRepository.save(testLabel2);

        TaskCreateDTO testTask2 = new TaskCreateDTO();
        testTask2.setTitle("TaskName1111");
        testTask2.setAssigneeId(testUser.getId());
        testTask2.setStatus(testStatus.getSlug());
        testTask2.setLabelsId(List.of(testLabel2.getId()));

        var token2 = jwt().jwt(builder -> builder.subject(testUser2.getEmail()));

        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testTask2))
                .with(token2);

        var result = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();

        var body = result.getResponse().getContentAsString();
        var id = om.readTree(body).get("id").asLong();
        assertThat(taskRepository.findById(id)).isPresent();

        var taskFromRepo = taskRepository.findById(id).get();

        assertThat(taskFromRepo).isNotNull();
        assertThat(taskFromRepo.getName()).isEqualTo(testTask2.getTitle());
    }

    @Test
    public void testUpdate() throws Exception {
        Map data = Map.of("title", "updateName");

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).get();
        assertThat(task.getName()).isEqualTo(("updateName"));

    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        var result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
