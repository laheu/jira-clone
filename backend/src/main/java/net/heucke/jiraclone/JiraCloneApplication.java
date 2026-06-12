package net.heucke.jiraclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JiraCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiraCloneApplication.class, args);
    }
}
