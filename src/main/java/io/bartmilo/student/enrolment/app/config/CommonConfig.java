package io.bartmilo.student.enrolment.app.config;

import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
  @Bean
  public Random random() {
    return new Random();
  }
}
