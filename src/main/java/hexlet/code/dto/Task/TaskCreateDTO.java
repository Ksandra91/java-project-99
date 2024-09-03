package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDTO {


    private Integer index;

    @NotNull
    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private String status;

    private List<Long> taskLabelIds;

}
