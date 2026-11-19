package se.helpdesk.helpdesk.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import se.helpdesk.helpdesk.dto.CreateCommentRequest;
import se.helpdesk.helpdesk.dto.CreateTicketRequest;
import se.helpdesk.helpdesk.entity.Comment;
import se.helpdesk.helpdesk.entity.Ticket;
import se.helpdesk.helpdesk.entity.User;
import se.helpdesk.helpdesk.service.CommentService;
import se.helpdesk.helpdesk.service.TicketService;
import se.helpdesk.helpdesk.service.UserService;

import java.util.List;

@Controller
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final CommentService commentService;

    public TicketController(
            TicketService ticketService,
            UserService userService,
            CommentService commentService) {

        this.ticketService = ticketService;
        this.userService = userService;
        this.commentService = commentService;
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

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);
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

    @GetMapping("/tickets/{id}")
    public String showTicketDetails(
            @PathVariable Long id,
            Model model,
            Authentication authentication
            ) {

        Ticket ticket = ticketService.getTicketById(id);
        model.addAttribute("ticket", ticket);

        List<Comment> comments = commentService.getCommentsByTicketId(id);
        model.addAttribute("comments", comments);

        model.addAttribute("createCommentRequest", new CreateCommentRequest(""));

        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_AGENT"));

        model.addAttribute("isStaff", isStaff);

        return "ticket-details";
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
