package se.helpdesk.helpdesk.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import se.helpdesk.helpdesk.dto.CreateCommentRequest;
import se.helpdesk.helpdesk.entity.Comment;
import se.helpdesk.helpdesk.entity.Ticket;
import se.helpdesk.helpdesk.entity.User;
import se.helpdesk.helpdesk.service.CommentService;
import se.helpdesk.helpdesk.service.TicketService;
import se.helpdesk.helpdesk.service.UserService;

import java.util.List;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final TicketService ticketService;
    private final UserService userService;

    public CommentController(CommentService commentService, TicketService ticketService, UserService userService) {
        this.commentService = commentService;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @PostMapping("/tickets/{id}/comments")
    public String createComment(
            @PathVariable Long id,
            @Valid @ModelAttribute CreateCommentRequest createCommentRequest,
            BindingResult bindingResult,
            Model model,
            Authentication authentication){

        if (bindingResult.hasErrors()){
            Ticket ticket = ticketService.getTicketById(id);
            model.addAttribute("ticket", ticket);

            List<Comment> comments = commentService.getCommentsByTicketId(id);
            model.addAttribute("comments", comments);

            return "ticket-details";
        }

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        Long userId = user.getId();

        commentService.createComment(createCommentRequest.text(), userId, id);

        return "redirect:/tickets/{id}";
    }
}
