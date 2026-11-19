package se.helpdesk.helpdesk.service;

import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public User getUserByIdOrThrow(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("user id can't be null");
        }

        return getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("user id can't be null"));
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

    @PreAuthorize("hasRole('ADMIN')")
    public void changeUserRole(Long id, UserRole newRole) {

        if (id == null) {
            throw new IllegalArgumentException("user id can't be null");
        }

        if (newRole == null) {
            throw new IllegalArgumentException("user role can't be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (user.getUsername().equals(authentication.getName())
                && newRole != UserRole.ADMIN) {

            throw new IllegalArgumentException(
                    "You cannot remove your own ADMIN role"
            );
        }

        user.changeRole(newRole);
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("user id can't be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (user.getUsername().equals(authentication.getName())) {
            throw new IllegalArgumentException(
                    "You can not de-avtivate your ownd account"
            );
        }

        user.deactivate();
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void activateUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("user id can't be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        user.activate();
        userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateUser(Long id, UserRole newRole, boolean active) {

        if (id == null) {
            throw new IllegalArgumentException("user id can't be null");
        }
        if (newRole == null) {
            throw new IllegalArgumentException("user role can't be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (user.getUsername().equals(authentication.getName())
                && newRole != UserRole.ADMIN) {
            throw new IllegalArgumentException(
                    "You cannot change your own ADMIN role"
            );
        }

        if (user.getUsername().equals(authentication.getName())
                && !active) {
            throw new IllegalArgumentException(
                    "Cannot de-activate your own account"
            );
        }

        user.changeRole(newRole);

        if (active){
            user.activate();
        } else {
            user.deactivate();
        }

        userRepository.save(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getUserByUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username can't be null or blank");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }
}
