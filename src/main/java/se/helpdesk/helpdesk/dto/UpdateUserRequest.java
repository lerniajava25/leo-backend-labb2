package se.helpdesk.helpdesk.dto;

import jakarta.validation.constraints.NotNull;
import se.helpdesk.helpdesk.enums.UserRole;

public record UpdateUserRequest(@NotNull UserRole role, boolean active) {
}
