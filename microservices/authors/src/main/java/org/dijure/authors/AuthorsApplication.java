package org.dijure.authors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@ImportResource("classpath:spring-beans-config.xml")
public class AuthorsApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(AuthorsApplication.class, args);
    }
}
