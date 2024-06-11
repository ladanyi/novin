package com.example.invoice_app.config;

import com.example.invoice_app.model.Role;
import com.example.invoice_app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("Standard user role");
            roleRepository.save(userRole);

            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("Administrator role");
            roleRepository.save(adminRole);

            Role accountantRole = new Role();
            accountantRole.setName("ROLE_ACCOUNTANT");
            accountantRole.setDescription("Accountant role");
            roleRepository.save(accountantRole);
        }
    }
}
