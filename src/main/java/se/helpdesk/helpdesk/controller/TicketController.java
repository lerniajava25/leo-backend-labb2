package se.helpdesk.helpdesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import se.helpdesk.helpdesk.service.TicketService;

@Controller
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public String listTickets(Model model) {
        model.addAttribute("pageTitle", "Mina ärenden");
        return "tickets";
    }
}
