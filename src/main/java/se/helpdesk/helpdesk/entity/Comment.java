package se.helpdesk.helpdesk.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Comment() {}

    public Comment(String text, User user, Ticket ticket) {
        this.text = text;
        this.user = user;
        this.ticket = ticket;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
