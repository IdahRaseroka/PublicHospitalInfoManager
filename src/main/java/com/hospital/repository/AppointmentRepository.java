package com.hospital.repository;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStatusAndAppointmentDateAfterOrderByAppointmentDate(AppointmentStatus status, LocalDateTime date);

    List<Appointment> findByPatientOrderByAppointmentDate(Patient patient);

    List<Appointment> findByPatientAndStatusAndAppointmentDateBetween(Patient patient, AppointmentStatus status,
                                                                      LocalDateTime start, LocalDateTime end);
}
