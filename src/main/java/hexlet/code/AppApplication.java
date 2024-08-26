package hexlet.code;

import io.sentry.Sentry;
import net.datafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {

        SpringApplication.run(AppApplication.class, args);

//        try {
//            throw new Exception("This is a test 222");
//        } catch (Exception e) {
//            Sentry.captureException(e);
//        }
    }

    @Bean
    public Faker getFaker() {
        return new Faker();
    }

}
