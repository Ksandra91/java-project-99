package hexlet.code.dto.Task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Long id;
    private Integer index;
    private String createdAt;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
    private List<Long> taskLabelIds;
}
