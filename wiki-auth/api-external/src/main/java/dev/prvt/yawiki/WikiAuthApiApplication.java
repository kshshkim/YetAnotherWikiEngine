package dev.prvt.yawiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class WikiAuthApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WikiAuthApiApplication.class, args);

    }
}
