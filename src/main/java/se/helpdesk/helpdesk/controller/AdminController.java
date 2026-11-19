package se.helpdesk.helpdesk.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.helpdesk.helpdesk.dto.CreateUserRequest;
import se.helpdesk.helpdesk.dto.UpdateUserRequest;
import se.helpdesk.helpdesk.entity.User;
import se.helpdesk.helpdesk.enums.UserRole;
import se.helpdesk.helpdesk.service.UserService;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {

        Iterable<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "admin-users";
    }

    @GetMapping("/admin/users/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateUserForm(Model model) {

        model.addAttribute(
                "createUserRequest",
                new CreateUserRequest("", "", null));

        return "create-user";
    }

    @PostMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser(
            @Valid @ModelAttribute CreateUserRequest createUserRequest,
            BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "create-user";
        }

        if (userService.usernameExists(createUserRequest.username())){

            bindingResult.rejectValue(
                    "username",
                    "username.exists",
                    "Användarnamn finns redan, försök igen.");

            return "create-user";
        }

        userService.createUser(
                createUserRequest.username(),
                createUserRequest.password(),
                createUserRequest.role());

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditUserForm(@PathVariable Long id, Model model) {

        User user = userService.getUserByIdOrThrow(id);

        UpdateUserRequest updateUserRequest =
                new UpdateUserRequest(user.getRole(), user.isActive());

        model.addAttribute("user", user);
        model.addAttribute("updateUserRequest", updateUserRequest);

        return "admin-user-edit";
    }

    @PostMapping("/admin/users/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute UpdateUserRequest updateUserRequest,
                             BindingResult bindingResult,
                             Model model){

        if(bindingResult.hasErrors()){
            User user = userService.getUserByIdOrThrow(id);
            model.addAttribute("user", user);
            return "admin-user-edit";
        }

        try {
            userService.updateUser(
                    id,
                    updateUserRequest.role(),
                    updateUserRequest.active()
            );

            return  "redirect:/admin/users";

        } catch (IllegalArgumentException e) {
            User user = userService.getUserByIdOrThrow(id);
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", e.getMessage());

            return "admin-user-edit";
        }
    }
}
