package se.helpdesk.helpdesk.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.helpdesk.helpdesk.entity.Ticket;
import se.helpdesk.helpdesk.entity.User;
import se.helpdesk.helpdesk.enums.TicketStatus;
import se.helpdesk.helpdesk.enums.UserRole;
import se.helpdesk.helpdesk.repository.TicketRepository;
import se.helpdesk.helpdesk.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("#username == authentication.name")
    public List<Ticket> getTicketsCreatedByUsername(String username) {

        if (username == null || username.isBlank()){
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        return ticketRepository.findByCreatedByUsername(username);
    }

    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public Iterable<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("id can't be null");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ticket with id " + id + " not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isOwner = ticket.getCreatedBy().getUsername().equals(authentication.getName());

        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                || a.getAuthority().equals("ROLE_AGENT"));

        if (!isOwner && !isStaff) {
            throw new AccessDeniedException("You are not allowed to access this ticket");
        }

        return ticket;
    }

    @PreAuthorize("hasRole('USER')")
    public Ticket createTicket(String title, String description, Long userId) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description cannot be null or blank");
        }

        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (user.getRole() != UserRole.USER) {
            throw new IllegalArgumentException("ticket can only be created by user");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("Ticket can only be created by active users");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isSelf = user.getUsername().equals(authentication.getName());

        if (!isSelf) {
            throw new AccessDeniedException("You are not allowed to create this ticket");
        }

        return ticketRepository.save(new Ticket(title, description, user));
    }

    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public void changeTicketStatus(Long id, TicketStatus ticketStatus) {

        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (ticketStatus == null) {
            throw new IllegalArgumentException("ticketStatus cannot be null");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket not found"));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User assignedAgent = ticket.getAssignedAgent();

        if (assignedAgent == null ||
                !assignedAgent.getUsername().equals(authentication.getName())) {
            throw new AccessDeniedException(
                    "You are not allowed to change this ticket's status");
        }

        ticket.changeStatus(ticketStatus);
        ticketRepository.save(ticket);
    }

    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public void assignAgent(Long ticketId, Long agentId) {

        if (ticketId == null) {
            throw new IllegalArgumentException("ticketId cannot be null");
        }

        if (agentId == null) {
            throw new IllegalArgumentException("agentId cannot be null");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket not found"));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agent not found"));

        if (agent.getRole() != UserRole.AGENT && agent.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Role has to be AGENT or ADMIN");
        }

        if (!agent.isActive()) {
            throw new IllegalArgumentException("Can't assign agent to inactive user");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isSelf = agent.getUsername().equals(authentication.getName());

        if (!isSelf){
            throw new AccessDeniedException("You are not allowed to change this ticket");
        }

        ticket.assignAgent(agent);
        ticketRepository.save(ticket);
    }
}
