package com.ecommerce.project.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_tasks")
@Getter
@Setter
public class ScheduledTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private LocalDateTime runAt;
    private String status = "PENDING";

}
