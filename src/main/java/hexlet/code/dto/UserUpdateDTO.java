package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateDTO {

    @NotNull
    private JsonNullable<String> firstName;

    @NotNull
    private JsonNullable<String> lastName;

    @NotNull
    private JsonNullable<String> email;

    @NotNull
    @Size(min = 3)
    private JsonNullable<String> password;
}
