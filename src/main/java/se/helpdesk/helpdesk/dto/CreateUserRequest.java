package se.helpdesk.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import se.helpdesk.helpdesk.enums.UserRole;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull UserRole role) {
}
