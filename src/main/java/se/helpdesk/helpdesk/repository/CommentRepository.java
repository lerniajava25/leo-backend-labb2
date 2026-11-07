package se.helpdesk.helpdesk.repository;

import org.springframework.data.repository.CrudRepository;
import se.helpdesk.helpdesk.entity.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {
}
