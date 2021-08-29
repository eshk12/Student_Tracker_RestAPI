package com.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableScheduling
public class Sample {
    public static boolean applicationStarted = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(Sample.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Sample.class, args);
        LOGGER.info("Application started.");
        applicationStarted = true;
    }
}
