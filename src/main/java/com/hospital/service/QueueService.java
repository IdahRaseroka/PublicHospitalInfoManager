package com.hospital.service;

import com.hospital.dto.CheckInRequest;
import com.hospital.dto.CheckInResponse;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.Category;
import com.hospital.model.Patient;
import com.hospital.model.Ticket;
import com.hospital.model.TicketStatus;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class QueueService {

    private final TicketRepository ticketRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;

    @Value("${hospital.nurse-code}")
    private String nurseCode;

    public QueueService(TicketRepository ticketRepository, AppointmentRepository appointmentRepository,
                        PatientService patientService) {
        this.ticketRepository = ticketRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
    }

    public CheckInResponse checkIn(CheckInRequest request) {
        Category category;
        try {
            category = Category.valueOf(request.getCategory());
        } catch (Exception e) {
            throw new RuntimeException("Please choose walk-in, appointment or emergency");
        }

        Patient patient;
        boolean newPatient = false;
        String idNumber = request.getIdNumber() == null ? "" : request.getIdNumber().trim();

        if (!idNumber.isEmpty()) {
            if (!patientService.isValidIdNumber(idNumber)) {
                throw new RuntimeException("ID number must be 13 digits");
            }
            if (request.getFullName() == null || request.getFullName().isBlank()) {
                throw new RuntimeException("Please enter your full name");
            }

            // returning patients are matched by ID so they keep the same file
            patient = patientService.findByIdNumber(idNumber);
            if (patient == null) {
                patient = patientService.createPatient(request.getFullName().trim(), idNumber);
                newPatient = true;
            }
        } else if (nurseCode.equals(request.getNurseCode())) {
            // no ID, a nurse entered the code word for this patient
            patient = patientService.createUnidentifiedPatient(request.getFullName());
            newPatient = true;
        } else {
            throw new RuntimeException("Please enter your ID number. If you have no ID ask a nurse to help you");
        }

        if (!ticketRepository.findByPatientAndStatus(patient, TicketStatus.WAITING).isEmpty()
                || !ticketRepository.findByPatientAndStatus(patient, TicketStatus.CALLED).isEmpty()) {
            throw new RuntimeException("You are already in the queue");
        }

        if (category == Category.APPOINTMENT && !hasAppointmentToday(patient)) {
            throw new RuntimeException("No appointment found for today. Please choose walk-in");
        }

        Ticket ticket = new Ticket();
        ticket.setPatient(patient);
        ticket.setCategory(category);
        ticket.setStatus(TicketStatus.WAITING);
        ticket.setTicketNumber(makeTicketNumber(category));
        ticket.setCheckInTime(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        CheckInResponse response = new CheckInResponse();
        response.setTicketNumber(ticket.getTicketNumber());
        response.setFileNumber(patient.getFileNumber());
        response.setTempCode(patient.getTempCode());
        response.setNewPatient(newPatient);
        response.setPeopleAhead(countPeopleAhead(ticket.getId()));
        return response;
    }

    private boolean hasAppointmentToday(Patient patient) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return !appointmentRepository.findByPatientAndStatusAndAppointmentDateBetween(
                patient, AppointmentStatus.BOOKED, start, end).isEmpty();
    }

    private String makeTicketNumber(Category category) {
        String letter;
        if (category == Category.EMERGENCY) {
            letter = "E";
        } else if (category == Category.APPOINTMENT) {
            letter = "A";
        } else {
            letter = "W";
        }
        long countToday = ticketRepository.countByCategoryAndCheckInTimeAfter(category, LocalDate.now().atStartOfDay());
        return letter + String.format("%03d", countToday + 1);
    }

    private int countPeopleAhead(Long ticketId) {
        int count = 0;
        for (Ticket t : getWaitingQueue()) {
            if (t.getId().equals(ticketId)) {
                break;
            }
            count++;
        }
        return count;
    }

    // emergency first, then appointment, then walk-in. Same category goes by check in time
    public List<Ticket> getWaitingQueue() {
        List<Ticket> waiting = ticketRepository.findByStatus(TicketStatus.WAITING);
        waiting.sort(Comparator.comparing(Ticket::getCategory).thenComparing(Ticket::getCheckInTime));
        return waiting;
    }

    public List<Ticket> getCalledTickets() {
        List<Ticket> called = ticketRepository.findByStatus(TicketStatus.CALLED);
        called.sort(Comparator.comparing(Ticket::getCalledTime));
        return called;
    }

    public Ticket callNext(String room) {
        if (room == null || room.isBlank()) {
            throw new RuntimeException("Please enter a room");
        }
        List<Ticket> waiting = getWaitingQueue();
        if (waiting.isEmpty()) {
            throw new RuntimeException("Nobody is waiting");
        }

        Ticket next = waiting.get(0);
        next.setStatus(TicketStatus.CALLED);
        next.setRoom(room);
        next.setCalledTime(LocalDateTime.now());
        return ticketRepository.save(next);
    }

    public Ticket finishVisit(Long ticketId, String notes) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatus(TicketStatus.DONE);
        ticket.setNotes(notes);
        ticket.setFinishedTime(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getPastVisits(Patient patient) {
        return ticketRepository.findByPatientAndStatusOrderByFinishedTimeDesc(patient, TicketStatus.DONE);
    }
}
