package se.helpdesk.helpdesk.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.helpdesk.helpdesk.entity.Ticket;
import se.helpdesk.helpdesk.entity.User;
import se.helpdesk.helpdesk.enums.TicketStatus;
import se.helpdesk.helpdesk.service.TicketService;
import se.helpdesk.helpdesk.service.UserService;

@Controller
public class AgentController {

    private final TicketService ticketService;
    private final UserService userService;

    public AgentController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping("/agent/tickets/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public String showEditTicketForm(@PathVariable Long id, Model model) {

        Ticket ticket = ticketService.getTicketById(id);
        model.addAttribute("ticket", ticket);

        return "agent-ticket-edit";
    }

    @PostMapping("/agent/tickets/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public String updateTicketStatus(@PathVariable Long id, @RequestParam("status") TicketStatus ticketStatus) {

        ticketService.changeTicketStatus(id, ticketStatus);

        return "redirect:/agent/tickets/{id}/edit";
    }

    @PostMapping("/agent/tickets/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public String assignTicketToSelf(@PathVariable Long id, Authentication authentication) {

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        ticketService.assignAgent(id, user.getId());

        return "redirect:/agent/tickets/{id}/edit";

    }
}
