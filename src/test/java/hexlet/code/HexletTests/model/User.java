package hexlet.code.HexletTests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private LocalDate updatedAt;
    private LocalDate createdAt;
}
