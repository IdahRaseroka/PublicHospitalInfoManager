package com.hospital.service;

import com.hospital.model.Patient;
import com.hospital.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // SA ID numbers are 13 digits
    public boolean isValidIdNumber(String idNumber) {
        return idNumber != null && idNumber.matches("\\d{13}");
    }

    public Patient findByIdNumber(String idNumber) {
        return patientRepository.findByIdNumber(idNumber);
    }

    public Patient createPatient(String fullName, String idNumber) {
        Patient patient = new Patient();
        patient.setFullName(fullName);
        patient.setIdNumber(idNumber);
        patient.setCreatedAt(LocalDateTime.now());
        patient = patientRepository.save(patient);

        // file number is made from the id so it is always unique
        patient.setFileNumber("HF" + String.format("%05d", patient.getId()));
        return patientRepository.save(patient);
    }

    // for unconscious patients or patients with no ID
    public Patient createUnidentifiedPatient(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            fullName = "Unknown patient";
        }
        Patient patient = createPatient(fullName, null);
        patient.setTempCode("TEMP-" + String.format("%04d", patient.getId()));
        return patientRepository.save(patient);
    }

    // search by ID number, temp code or file number
    public Patient search(String value) {
        value = value.trim();
        Patient patient = patientRepository.findByIdNumber(value);
        if (patient == null) {
            patient = patientRepository.findByTempCode(value.toUpperCase());
        }
        if (patient == null) {
            patient = patientRepository.findByFileNumber(value.toUpperCase());
        }
        return patient;
    }

    // when the ID of an unknown patient is found we add it to their file
    public Patient linkIdNumber(Long patientId, String idNumber, String fullName) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (!isValidIdNumber(idNumber)) {
            throw new RuntimeException("ID number must be 13 digits");
        }
        if (patientRepository.findByIdNumber(idNumber) != null) {
            throw new RuntimeException("This ID number already belongs to another file");
        }

        patient.setIdNumber(idNumber);
        if (fullName != null && !fullName.isBlank()) {
            patient.setFullName(fullName);
        }
        return patientRepository.save(patient);
    }
}
