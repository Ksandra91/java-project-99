package hexlet.code.dto.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusDTO {
    private long id;
    private String name;
    private String slug;
    private String createdAt;
}
