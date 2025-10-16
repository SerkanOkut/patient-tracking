package com.dentalclinic.patient_tracking.service;

import com.dentalclinic.patient_tracking.dto.PatientDTO;
import com.dentalclinic.patient_tracking.entity.Patient;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.enums.Role;
import com.dentalclinic.patient_tracking.exception.AccessDeniedException;
import com.dentalclinic.patient_tracking.exception.NotFoundException;
import com.dentalclinic.patient_tracking.repository.PatientRepository;
import com.dentalclinic.patient_tracking.repository.TreatmentRecordRepository;
import com.dentalclinic.patient_tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final TreatmentRecordRepository treatmentRecordRepository;
    private final UserRepository userRepository;

    /**
     * Tek bir hasta kaydını alır ve rol bazlı erişimi kontrol eder
     */
    public PatientDTO getPatientById(Long patientId, User currentUser) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        checkAccess(patient, currentUser);

        return mapToDTO(patient);
    }

    /**
     * Tüm hastaları listeler, rol bazlı filtreleme yapılır
     */
    public List<PatientDTO> getAllPatients(User currentUser) {
        List<Patient> patients;

        if (currentUser.getRole() == Role.ADMIN) {
            patients = patientRepository.findAll();
        } else if (currentUser.getRole() == Role.DOCTOR) {
            Long doctorId = currentUser.getDoctor().getId();
            patients = treatmentRecordRepository.findPatientsByDoctorId(doctorId);
        } else if (currentUser.getRole() == Role.PATIENT) {
            patients = List.of(currentUser.getPatient());
        } else {
            throw new AccessDeniedException("Geçersiz rol");
        }

        return patients.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Borç artırma veya azaltma işlemi
     */
    @Transactional
    public void updateGeneralDebt(Long patientId, BigDecimal amount) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        BigDecimal newDebt = patient.getGeneralDebt().add(amount);
        if (newDebt.compareTo(BigDecimal.ZERO) < 0) {
            newDebt = BigDecimal.ZERO;
        }

        patient.setGeneralDebt(newDebt);
        patientRepository.save(patient);
    }

    /**
     * Hasta borcunu döndürür
     */
    public BigDecimal getPatientDebt(Long patientId, User currentUser) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        checkAccess(patient, currentUser);

        return patient.getGeneralDebt();
    }

    /**
     * Erişim kontrolü
     */
    private void checkAccess(Patient patient, User currentUser) {
        if (currentUser.getRole() == Role.DOCTOR) {
            Long doctorId = currentUser.getDoctor().getId();
            boolean hasAccess = treatmentRecordRepository.existsByPatientIdAndDoctorId(patient.getId(), doctorId);
            if (!hasAccess) {
                throw new AccessDeniedException("Bu hastaya erişim yetkiniz yok");
            }
        } else if (currentUser.getRole() == Role.PATIENT) {
            if (!patient.getId().equals(currentUser.getPatient().getId())) {
                throw new AccessDeniedException("Bu hastaya erişim yetkiniz yok");
            }
        }
    }

    /**
     * Entity → DTO dönüşümü
     */
    private PatientDTO mapToDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .tcNumber(patient.getTcNumber())
                .phoneNumber(patient.getPhoneNumber())
                .generalDebt(patient.getGeneralDebt())
                .activeClinicName(patient.getActiveClinic().getName())
                .build();
    }
}
