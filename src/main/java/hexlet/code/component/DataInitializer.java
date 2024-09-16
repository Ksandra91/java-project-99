package hexlet.code.component;


import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import hexlet.code.service.CustomUserDetailsService;
import hexlet.code.model.User;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final TaskStatusRepository statusRepository;
    private final LabelRepository labelRepository;
    private final CustomUserDetailsService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        var userData = new User();
        userData.setEmail(email);
        userData.setFirstName("Admin");
        userData.setLastName("Admin");
        userData.setPasswordDigest("qwerty");
        userService.createUser(userData);

        TaskStatus status1 = new TaskStatus();
        status1.setName("Draft");
        status1.setSlug("draft");
        statusRepository.save(status1);

        TaskStatus status2 = new TaskStatus();
        status2.setName("Review");
        status2.setSlug("to_review");
        statusRepository.save(status2);

        TaskStatus status3 = new TaskStatus();
        status3.setName("ToBeFixed");
        status3.setSlug("to_be_fixed");
        statusRepository.save(status3);

        TaskStatus status4 = new TaskStatus();
        status4.setName("ToPublish");
        status4.setSlug("to_publish");
        statusRepository.save(status4);

        TaskStatus status5 = new TaskStatus();
        status5.setName("Published");
        status5.setSlug("published");
        statusRepository.save(status5);

        Label label1 = new Label();
        label1.setName("feature");
        labelRepository.save(label1);

        Label label2 = new Label();
        label2.setName("bug");
        labelRepository.save(label2);
    }
}
