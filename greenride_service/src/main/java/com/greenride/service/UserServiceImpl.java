package com.greenride.service;

import com.greenride.dto.RegisterRequest;
import com.greenride.model.Role;
import com.greenride.model.User;
import com.greenride.repository.RoleRepository;
import com.greenride.repository.UserRepository;
import com.greenride.service.port.SmsNotificationPort; // <--- Import the Port
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsNotificationPort smsNotificationPort; // <--- Use the Port Interface

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           SmsNotificationPort smsNotificationPort) { // <--- Inject the Port
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.smsNotificationPort = smsNotificationPort;
    }

    @Override
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        if (registerRequest == null) throw new IllegalArgumentException("Request cannot be null");

        String username = registerRequest.username().strip();
        String email = registerRequest.email().strip();
        String password = registerRequest.password();
        String phoneNumber = registerRequest.phoneNumber().strip();

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Role 'ROLE_USER' not found."));

        user.setRoles(Set.of(userRole));
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        // Send SMS using the new Port/Adapter
        smsNotificationPort.sendSms(savedUser.getPhoneNumber(), "Welcome to GreenRide! Your account is active.");

        return savedUser;
    }
}