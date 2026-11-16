package se.helpdesk.helpdesk.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.helpdesk.helpdesk.enums.UserRole;
import se.helpdesk.helpdesk.service.UserService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeDatabase(UserService userService) {
        return args -> {
            if (!userService.usernameExists("admin")) {

                String password = System.getenv("HELPDESK_ADMIN_PASSWORD");

                if (password == null || password.isBlank()) {
                    throw new IllegalStateException("HELPDESK_ADMIN_PASSWORD required");
                }

                userService.createUser("admin", password, UserRole.ADMIN);
            }

            if (!userService.usernameExists("testuser")) {

                String password = System.getenv("HELPDESK_TEST_USER_PASSWORD");

                if (password == null || password.isBlank()) {
                    throw new IllegalStateException("HELPDESK_TEST_USER_PASSWORD required");
                }

                userService.createUser("testuser", password, UserRole.USER);
            }

        };
    }
}
