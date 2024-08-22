package hexlet.code.dto.TaskStatus;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TaskStatusDTO {
    //  private Long id;
    @Size(min = 1)
    private String name;
    @Size(min = 1)
    private String slug;
    //  private LocalDate createdAt;
}
