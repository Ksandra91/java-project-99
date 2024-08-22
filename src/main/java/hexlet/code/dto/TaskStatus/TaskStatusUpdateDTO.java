package hexlet.code.dto.TaskStatus;

import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskStatusUpdateDTO {

    // @NotNull
    @Size(min = 1)
    private JsonNullable<String> name;

    // @NotNull
    @Size(min = 1)
    private JsonNullable<String> slug;

}
