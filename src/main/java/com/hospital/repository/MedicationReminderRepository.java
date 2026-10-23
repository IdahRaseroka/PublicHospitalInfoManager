package com.hospital.repository;

import com.hospital.model.MedicationReminder;
import com.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationReminderRepository extends JpaRepository<MedicationReminder, Long> {

    List<MedicationReminder> findByPatientOrderByReminderTime(Patient patient);
}
