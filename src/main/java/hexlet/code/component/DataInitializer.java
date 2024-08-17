package hexlet.code.component;


import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import hexlet.code.service.CustomUserDetailsService;
import hexlet.code.model.User;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TaskStatusRepository statusRepository;

    @Autowired
    private final CustomUserDetailsService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        var userData = new User();
        userData.setEmail(email);
        userData.setPasswordDigest("qwerty");
        userService.createUser(userData);

        TaskStatus status1 = new TaskStatus();
        status1.setName("Draft");
        status1.setSlug("draft");
        statusRepository.save(status1);

        TaskStatus status2 = new TaskStatus();
        status1.setName("Review");
        status1.setSlug("to_review");
        statusRepository.save(status2);

        TaskStatus status3 = new TaskStatus();
        status1.setName("ToBeFixed");
        status1.setSlug("to_be_fixed");
        statusRepository.save(status3);

        TaskStatus status4 = new TaskStatus();
        status1.setName("ToPublish");
        status1.setSlug("to_publish");
        statusRepository.save(status4);

        TaskStatus status5 = new TaskStatus();
        status1.setName("Published");
        status1.setSlug("published");
        statusRepository.save(status5);


    }
}
