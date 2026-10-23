package com.hospital.controller;

import com.hospital.model.Appointment;
import com.hospital.model.MedicationReminder;
import com.hospital.model.Patient;
import com.hospital.model.Ticket;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.MedicationReminderRepository;
import com.hospital.service.PatientService;
import com.hospital.service.QueueService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final QueueService queueService;
    private final AppointmentRepository appointmentRepository;
    private final MedicationReminderRepository reminderRepository;

    public PatientController(PatientService patientService, QueueService queueService,
                             AppointmentRepository appointmentRepository,
                             MedicationReminderRepository reminderRepository) {
        this.patientService = patientService;
        this.queueService = queueService;
        this.appointmentRepository = appointmentRepository;
        this.reminderRepository = reminderRepository;
    }

    // staff search by ID number, temp code or file number
    @GetMapping("/search")
    public Patient search(@RequestParam String value) {
        Patient patient = patientService.search(value);
        if (patient == null) {
            throw new RuntimeException("No patient file found");
        }
        return patient;
    }

    @PutMapping("/{id}/link-id")
    public Patient linkIdNumber(@PathVariable Long id, @RequestParam String idNumber,
                                @RequestParam(required = false) String fullName) {
        return patientService.linkIdNumber(id, idNumber, fullName);
    }

    // the endpoints below are for the patient page, the patient logs in with their ID number

    @GetMapping("/{idNumber}")
    public Patient getPatient(@PathVariable String idNumber) {
        return getPatientOrError(idNumber);
    }

    @GetMapping("/{idNumber}/visits")
    public List<Ticket> getVisits(@PathVariable String idNumber) {
        return queueService.getPastVisits(getPatientOrError(idNumber));
    }

    @GetMapping("/{idNumber}/appointments")
    public List<Appointment> getAppointments(@PathVariable String idNumber) {
        return appointmentRepository.findByPatientOrderByAppointmentDate(getPatientOrError(idNumber));
    }

    @GetMapping("/{idNumber}/reminders")
    public List<MedicationReminder> getReminders(@PathVariable String idNumber) {
        return reminderRepository.findByPatientOrderByReminderTime(getPatientOrError(idNumber));
    }

    @PostMapping("/{idNumber}/reminders")
    public MedicationReminder addReminder(@PathVariable String idNumber, @RequestBody MedicationReminder reminder) {
        if (reminder.getMedicineName() == null || reminder.getMedicineName().isBlank()) {
            throw new RuntimeException("Please enter the medicine name");
        }
        if (reminder.getReminderTime() == null) {
            throw new RuntimeException("Please choose a time");
        }
        reminder.setId(null);
        reminder.setPatient(getPatientOrError(idNumber));
        return reminderRepository.save(reminder);
    }

    @DeleteMapping("/{idNumber}/reminders/{reminderId}")
    public void deleteReminder(@PathVariable String idNumber, @PathVariable Long reminderId) {
        Patient patient = getPatientOrError(idNumber);
        MedicationReminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        // make sure patients can only delete their own reminders
        if (!reminder.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("Reminder not found");
        }
        reminderRepository.delete(reminder);
    }

    private Patient getPatientOrError(String idNumber) {
        Patient patient = patientService.findByIdNumber(idNumber);
        if (patient == null) {
            throw new RuntimeException("No patient file found for this ID number");
        }
        return patient;
    }
}
