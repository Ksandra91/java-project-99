package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskStatusUpdateDTO {

    @NotNull
    @Size(min = 3)
    private JsonNullable<String> name;

    @NotNull
    @Size(min = 3)
    private JsonNullable<String> slug;

}
