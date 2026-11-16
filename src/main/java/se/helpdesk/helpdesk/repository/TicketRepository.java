package se.helpdesk.helpdesk.repository;

import org.springframework.data.repository.CrudRepository;
import se.helpdesk.helpdesk.entity.Ticket;

import java.util.List;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
    List<Ticket> findByCreatedByUsername(String username);
}
