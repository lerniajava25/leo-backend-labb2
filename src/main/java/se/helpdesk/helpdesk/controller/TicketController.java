package se.helpdesk.helpdesk.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.helpdesk.helpdesk.dto.CreateTicketRequest;
import se.helpdesk.helpdesk.entity.Ticket;
import se.helpdesk.helpdesk.entity.User;
import se.helpdesk.helpdesk.service.TicketService;
import se.helpdesk.helpdesk.service.UserService;

@Controller
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping("/tickets")
    public String listTickets(Model model, Authentication authentication) {
        String username = authentication.getName();
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_AGENT"));

        Iterable<Ticket> tickets;

        if (isStaff) {
            tickets = ticketService.getAllTickets();
        } else {
            tickets = ticketService.getTicketsCreatedByUsername(username);
        }

        model.addAttribute("pageTitle", "Mina ärenden");
        model.addAttribute("tickets", tickets);
        return "tickets";
    }

    @GetMapping("/tickets/new")
    @PreAuthorize("hasRole('USER')")
    public String showCreateTicketForm(Model model) {
        model.addAttribute(
                "createTicketRequest",
                new CreateTicketRequest("", "")
        );

        return "new-ticket";
    }

    @PostMapping("/tickets")
    @PreAuthorize("hasRole('USER')")
    public String createTicket(
            @ModelAttribute @Valid CreateTicketRequest createTicketRequest,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            return "new-ticket";
        }

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        ticketService.createTicket(
                createTicketRequest.title(),
                createTicketRequest.description(),
                user.getId());

        return "redirect:/tickets";
    }
}
