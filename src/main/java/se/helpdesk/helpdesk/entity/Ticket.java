package se.helpdesk.helpdesk.entity;

import jakarta.persistence.*;
import se.helpdesk.helpdesk.enums.TicketStatus;

import java.time.LocalDateTime;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
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
        if (assignedAgent != null && this.assignedAgent == null) {
            this.assignedAgent = assignedAgent;
            this.status = TicketStatus.IN_PROGRESS;
        }
    }

    public void changeStatus(TicketStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status can't be null");
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
