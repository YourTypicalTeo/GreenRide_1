package com.greenride;

import com.greenride.model.Role;
import com.greenride.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GreenrideServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenrideServiceApplication.class, args);
    }

    // This runs automatically every time the server starts
    // It creates the necessary Roles in the DB so you don't get the 500 Error
    @Bean
    public CommandLineRunner init(RoleRepository roleRepository) {
        return args -> {
            // Check if ROLE_USER exists; if not, create it
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
                System.out.println("SYSTEM: Created ROLE_USER in database.");
            }

            // Check if ROLE_ADMIN exists; if not, create it
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
                System.out.println("SYSTEM: Created ROLE_ADMIN in database.");
            }
        };
    }
}