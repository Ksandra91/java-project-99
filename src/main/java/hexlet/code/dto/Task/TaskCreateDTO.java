package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskCreateDTO {

    @NotNull
    private String title;
    private int index;
    private String content;
    @NotNull
    private String status;
   // @NotNull
    @JsonProperty("assignee_id")
    private long assigneeId;

}
