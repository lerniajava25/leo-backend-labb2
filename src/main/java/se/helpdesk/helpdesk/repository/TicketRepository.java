package se.helpdesk.helpdesk.repository;

import org.springframework.data.repository.CrudRepository;
import se.helpdesk.helpdesk.entity.Ticket;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
}
