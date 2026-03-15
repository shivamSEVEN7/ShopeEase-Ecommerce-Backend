package com.ecommerce.project.repositories;

import com.ecommerce.project.model.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduledTaskRepo extends JpaRepository<ScheduledTask, Long> {
    List<ScheduledTask> findByStatus(String status);

    List<ScheduledTask> findAllByStatus(String status);
}

