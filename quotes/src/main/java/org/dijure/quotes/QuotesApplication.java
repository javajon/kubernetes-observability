package org.dijure.quotes;

import org.dijure.quotes.service.CorrelationIdInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@ImportResource("classpath:spring-beans-config.xml")
public class QuotesApplication
{
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate() {
            {
                setInterceptors(Collections.singletonList(new CorrelationIdInterceptor()));
            }
        };
    }

    public static void main(String[] args)
    {
        SpringApplication.run(QuotesApplication.class, args);
    }
}
