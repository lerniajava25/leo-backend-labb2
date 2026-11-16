package se.helpdesk.helpdesk.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import se.helpdesk.helpdesk.entity.Ticket;
import se.helpdesk.helpdesk.service.TicketService;

@Controller
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
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
}
