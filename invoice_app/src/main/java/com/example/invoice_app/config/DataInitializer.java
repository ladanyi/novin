package com.example.invoice_app.config;

import com.example.invoice_app.model.AppUser;
import com.example.invoice_app.model.Invoice;
import com.example.invoice_app.model.Role;
import com.example.invoice_app.repository.RoleRepository;
import com.example.invoice_app.service.InvoiceService;
import com.example.invoice_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

            AppUser adminUser = new AppUser();
            adminUser.setUsername("admin");
            adminUser.setPassword("admin1234");
            adminUser.setRoles(Set.of(adminRole));
            userService.saveUser(adminUser);

            AppUser accountantUser = new AppUser();
            accountantUser.setUsername("accountant");
            accountantUser.setPassword("accountant1234");
            accountantUser.setRoles(Set.of(accountantRole));
            userService.saveUser(accountantUser);

            AppUser generalUser = new AppUser();
            generalUser.setUsername("user");
            generalUser.setPassword("user1234");
            generalUser.setRoles(Set.of(userRole));
            userService.saveUser(generalUser);

            // Create invoices
            Invoice invoice1 = new Invoice();
            invoice1.setBuyer("Buyer1");
            invoice1.setIssueDate(LocalDate.of(2024, 1, 1));
            invoice1.setDueDate(LocalDate.of(2024, 1, 15));
            invoice1.setItem("Item1");
            invoice1.setComment("Comment1");
            invoice1.setPrice(100.0);
            invoiceService.saveInvoice(invoice1);

            Invoice invoice2 = new Invoice();
            invoice2.setBuyer("Buyer2");
            invoice2.setIssueDate(LocalDate.of(2024, 2, 1));
            invoice2.setDueDate(LocalDate.of(2024, 2, 15));
            invoice2.setItem("Item2");
            invoice2.setComment("Comment2");
            invoice2.setPrice(200.0);
            invoiceService.saveInvoice(invoice2);

            Invoice invoice3 = new Invoice();
            invoice3.setBuyer("Buyer3");
            invoice3.setIssueDate(LocalDate.of(2024, 3, 1));
            invoice3.setDueDate(LocalDate.of(2024, 3, 15));
            invoice3.setItem("Item3");
            invoice3.setComment("Comment3");
            invoice3.setPrice(300.0);
            invoiceService.saveInvoice(invoice3);
        }
    }
}
