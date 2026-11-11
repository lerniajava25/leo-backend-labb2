package se.helpdesk.helpdesk.service;

import org.springframework.stereotype.Service;
import se.helpdesk.helpdesk.entity.Comment;
import se.helpdesk.helpdesk.entity.Ticket;
import se.helpdesk.helpdesk.entity.User;
import se.helpdesk.helpdesk.enums.UserRole;
import se.helpdesk.helpdesk.repository.CommentRepository;
import se.helpdesk.helpdesk.repository.TicketRepository;
import se.helpdesk.helpdesk.repository.UserRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public CommentService(
            CommentRepository commentRepository,
            UserRepository userRepository,
            TicketRepository ticketRepository) {

        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    public Comment createComment(String text, Long userId, Long ticketId) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Invalid comment");
        }

        if (userId == null) {
            throw new IllegalArgumentException("user id can't be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Inactive user, cannot create comment");
        }

        if (ticketId == null) {
            throw new IllegalArgumentException("ticket id can't be null");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("ticket not found"));

        if (user.getRole() == UserRole.USER
                && !user.getId().equals(ticket.getCreatedBy().getId())) {
            throw new IllegalArgumentException("Invalid user, cannot create comment");
        }

        Comment comment = new Comment(text, user, ticket);
        return commentRepository.save(comment);
    }
}
