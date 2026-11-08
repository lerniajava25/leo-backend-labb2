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
        return ticketRepository.findById(id);
    }

    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public void changeTicketStatus(Long id, TicketStatus ticketStatus) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        ticket.changeStatus(ticketStatus);
        ticketRepository.save(ticket);
    }

    public void assignAgent(Long ticketId, Long agentId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        User agent =  userRepository.findById(agentId).orElseThrow();

        if (agent.getRole() != UserRole.AGENT) {
            throw new  IllegalArgumentException("Role has to be AGENT");
        }

        if (!agent.isActive()) {
            throw new  IllegalArgumentException("Can't assign agent to inactive user");
        }

        ticket.assignAgent(agent);
        ticketRepository.save(ticket);
    }
}
