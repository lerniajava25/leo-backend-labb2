package se.helpdesk.helpdesk.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.helpdesk.helpdesk.entity.User;
import se.helpdesk.helpdesk.enums.UserRole;
import se.helpdesk.helpdesk.repository.UserRepository;

import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("user id can't be null");
        }

        return userRepository.findById(id);

    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(String username, String password, UserRole role) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("user name can't be null or blank");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password can't be null or blank");
        }

        if (role == null) {
            throw new IllegalArgumentException("user role can't be null");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("username already exists");
        }

        String encodedPassword = passwordEncoder.encode(password);

        return userRepository.save(new User(username, encodedPassword, role));
    }

    public void changeUserRole(Long id, UserRole newRole) {

        if (id == null) {
            throw new IllegalArgumentException("user id can't be null");
        }

        if (newRole == null) {
            throw new IllegalArgumentException("user role can't be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        user.changeRole(newRole);
        userRepository.save(user);
    }

    public void deactivateUser(Long id){

        if (id == null){
            throw new IllegalArgumentException("user id can't be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        user.deactivate();
        userRepository.save(user);
    }

    public void activateUser(Long id){
        if (id == null){
            throw new IllegalArgumentException("user id can't be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        user.activate();
        userRepository.save(user);
    }
}
