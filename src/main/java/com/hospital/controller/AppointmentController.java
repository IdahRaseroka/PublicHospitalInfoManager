package com.hospital.controller;

import com.hospital.dto.AppointmentRequest;
import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.Patient;
import com.hospital.repository.AppointmentRepository;
import com.hospital.service.PatientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;

    public AppointmentController(AppointmentRepository appointmentRepository, PatientService patientService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
    }

    // all booked appointments from today onwards
    @GetMapping
    public List<Appointment> getUpcoming() {
        return appointmentRepository.findByStatusAndAppointmentDateAfterOrderByAppointmentDate(
                AppointmentStatus.BOOKED, LocalDate.now().atStartOfDay());
    }

    @PostMapping
    public Appointment book(@RequestBody AppointmentRequest request) {
        if (!patientService.isValidIdNumber(request.getIdNumber())) {
            throw new RuntimeException("ID number must be 13 digits");
        }
        if (request.getDoctorName() == null || request.getDoctorName().isBlank()) {
            throw new RuntimeException("Please enter the doctor name");
        }
        if (request.getAppointmentDate() == null || request.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Please choose a date and time in the future");
        }

        // if the patient has never been to the hospital we open a new file
        Patient patient = patientService.findByIdNumber(request.getIdNumber());
        if (patient == null) {
            if (request.getFullName() == null || request.getFullName().isBlank()) {
                throw new RuntimeException("New patient, please enter their full name");
            }
            patient = patientService.createPatient(request.getFullName().trim(), request.getIdNumber());
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctorName(request.getDoctorName().trim());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(AppointmentStatus.BOOKED);
        return appointmentRepository.save(appointment);
    }

    @PutMapping("/{id}/cancel")
    public Appointment cancel(@PathVariable Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }
}
