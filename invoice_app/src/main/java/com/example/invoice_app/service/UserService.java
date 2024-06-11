package com.example.invoice_app.service;

import com.example.invoice_app.model.AppUser;
import com.example.invoice_app.model.Role;
import com.example.invoice_app.repository.RoleRepository;
import com.example.invoice_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public AppUser saveUser(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userRepository.save(appUser);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void addRoleToUser(String username, String roleName) {
        Optional<AppUser> user = userRepository.findByUsername(username);
        Optional<Role> role = roleRepository.findByName(roleName);

        if (user.isPresent() && role.isPresent()) {
            Set<Role> roles = new HashSet<>(user.get().getRoles());
            roles.add(role.get());
            user.get().setRoles(roles);
            userRepository.save(user.get());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    public List<AppUser> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void updateUserRoles(Long id, List<String> roles) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<Role> roleSet = roles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());
        user.setRoles(roleSet);
        userRepository.save(user);
    }
}
