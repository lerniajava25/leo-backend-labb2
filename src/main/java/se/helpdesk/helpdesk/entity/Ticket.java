package se.helpdesk.helpdesk.entity;

import jakarta.persistence.*;
import se.helpdesk.helpdesk.enums.TicketStatus;

import java.time.LocalDateTime;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User assignedAgent;

    protected Ticket() {
    }

    public Ticket(
            String title,
            String description,
            User createdBy
    ) {
        this.title = title;
        this.description = description;
        this.status = TicketStatus.NEW;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public User getAssignedAgent() {
        return assignedAgent;
    }

    public void assignAgent(User assignedAgent) {

        if (assignedAgent == null) {
            throw new IllegalArgumentException("assignedAgent cannot be null");
        }

        if (this.status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Can't assign agent to closed ticket");
        }

        if (this.status == TicketStatus.RESOLVED){
            throw new IllegalStateException("Can't assign agent to resolved ticket");
        }

        if (this.assignedAgent != null) {
            throw new IllegalStateException("Agent is already assigned");
        }

        this.assignedAgent = assignedAgent;
        this.status = TicketStatus.IN_PROGRESS;
    }

    public void changeStatus(TicketStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status can't be null");
        }

        if (this.status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Status CLOSED can't be changed");
        }

        if (newStatus == TicketStatus.CLOSED && assignedAgent == null) {
            throw new IllegalStateException("Status can't be CLOSED without an assigned agent");
        }

        if (newStatus == TicketStatus.RESOLVED && assignedAgent == null) {
            throw new IllegalStateException("Status can't be RESOLVED without an assigned agent");
        }

        if (newStatus == TicketStatus.IN_PROGRESS && assignedAgent == null) {
            throw new IllegalStateException("Status can't be IN_PROGRESS without an assigned agent");
        }

        if (newStatus == this.status) {
            throw new IllegalStateException("Status already is " + newStatus);
        }

        if (newStatus == TicketStatus.NEW && assignedAgent != null) {
            throw new IllegalStateException("Status can't be NEW when agent is already assigned");
        }

        this.status = newStatus;
    }

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
