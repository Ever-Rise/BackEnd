package br.com.everrise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EverriseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EverriseBackendApplication.class, args);
    }
}
