package com.hospital.repository;

import com.hospital.model.Category;
import com.hospital.model.Patient;
import com.hospital.model.Ticket;
import com.hospital.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByPatientAndStatus(Patient patient, TicketStatus status);

    List<Ticket> findByPatientAndStatusOrderByFinishedTimeDesc(Patient patient, TicketStatus status);

    // used to make the ticket number e.g. W004 is the 4th walk-in today
    long countByCategoryAndCheckInTimeAfter(Category category, LocalDateTime time);
}
