package it.wat.progettoingsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ProgettoIngSoftApplication {
    public static void main(String[] args) {
        ApplicationContext context =  SpringApplication.run(ProgettoIngSoftApplication.class, args);
    }
}