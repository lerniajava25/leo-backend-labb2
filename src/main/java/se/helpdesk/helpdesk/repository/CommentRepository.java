package se.helpdesk.helpdesk.repository;

import org.springframework.data.repository.CrudRepository;
import se.helpdesk.helpdesk.entity.Comment;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByTicketId(Long id);
}
