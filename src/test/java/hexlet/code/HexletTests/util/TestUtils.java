package hexlet.code.HexletTests.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import hexlet.code.HexletTests.model.TaskStatus;
import hexlet.code.HexletTests.model.Label;
import hexlet.code.HexletTests.model.Task;
import hexlet.code.HexletTests.model.User;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.List;


public class TestUtils {
    private static ObjectMapper om = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static void saveLabel(MockMvc mockMvc, Label label) throws Exception {
        mockMvc.perform(post("/api/labels").with(jwt())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(label)))
                .andReturn();
    }

    public static void saveTaskStatus(MockMvc mockMvc, TaskStatus taskStatus) throws Exception {
        mockMvc.perform(post("/api/task_statuses").with(jwt())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(taskStatus)))
                .andReturn();
    }

    public static void saveUser(MockMvc mockMvc, User user) throws Exception {
        mockMvc.perform(post("/api/users").with(jwt())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(user)))
                .andReturn();
    }

    public static void saveTask(MockMvc mockMvc, Task task) throws Exception {
        mockMvc.perform(post("/api/tasks").with(jwt())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(task)))
                .andReturn();
    }

    public static User getUserByEmail(MockMvc mockMvc, String email) throws Exception {
        var response = mockMvc.perform(get("/api/users").with(jwt()))
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        var users = om.readValue(body, new TypeReference<List<User>>() { });

        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public static Label getLabelByName(MockMvc mockMvc, String name) throws Exception {
        var response = mockMvc.perform(get("/api/labels").with(jwt()))
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        var labels = om.readValue(body, new TypeReference<List<Label>>() { });

        return labels.stream()
                .filter(label -> label.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static TaskStatus getStatusByName(MockMvc mockMvc, String name) throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        var statuses = om.readValue(body, new TypeReference<List<TaskStatus>>() { });

        return statuses.stream()
                .filter(status -> status.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static Task getTaskByName(MockMvc mockMvc, String name) throws Exception {
        var response = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        var tasks = om.readValue(body, new TypeReference<List<Task>>() { });

        return tasks.stream()
                .filter(task -> task.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
