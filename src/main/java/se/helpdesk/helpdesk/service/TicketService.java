package se.helpdesk.helpdesk.service;

import org.springframework.stereotype.Service;
import se.helpdesk.helpdesk.entity.Ticket;
import se.helpdesk.helpdesk.entity.User;
import se.helpdesk.helpdesk.enums.TicketStatus;
import se.helpdesk.helpdesk.enums.UserRole;
import se.helpdesk.helpdesk.repository.TicketRepository;
import se.helpdesk.helpdesk.repository.UserRepository;

import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public Iterable<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id){

        if (id == null){
            throw new IllegalArgumentException("id can't be null");
        }

        return ticketRepository.findById(id);
    }

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

        User user = userRepository.findById(userId).orElseThrow();

        if (user.getRole() != UserRole.USER) {
            throw new IllegalArgumentException("ticket can only be created by user");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("Ticket can only be created by active users");
        }

        return ticketRepository.save(new Ticket(title, description, user));
    }

    public void changeTicketStatus(Long id, TicketStatus ticketStatus) {

        if (id == null){
            throw new IllegalArgumentException("id cannot be null");
        }

        if (ticketStatus == null){
            throw new IllegalArgumentException("ticketStatus cannot be null");
        }

        Ticket ticket = ticketRepository.findById(id).orElseThrow();

        ticket.changeStatus(ticketStatus);
        ticketRepository.save(ticket);
    }

    public void assignAgent(Long ticketId, Long agentId) {

        if (ticketId == null) {
            throw new IllegalArgumentException("ticketId cannot be null");
        }

        if (agentId == null) {
            throw new IllegalArgumentException("agentId cannot be null");
        }

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        User agent =  userRepository.findById(agentId).orElseThrow();

        if (agent.getRole() != UserRole.AGENT && agent.getRole() != UserRole.ADMIN) {
            throw new  IllegalArgumentException("Role has to be AGENT or ADMIN");
        }

        if (!agent.isActive()) {
            throw new  IllegalArgumentException("Can't assign agent to inactive user");
        }

        ticket.assignAgent(agent);
        ticketRepository.save(ticket);
    }
}
