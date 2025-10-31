package com.dentalclinic.patient_tracking.service;

import com.dentalclinic.patient_tracking.dto.ClinicDTO;
import com.dentalclinic.patient_tracking.entity.Clinic;
import com.dentalclinic.patient_tracking.entity.Doctor;
import com.dentalclinic.patient_tracking.entity.Patient;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.enums.Role;
import com.dentalclinic.patient_tracking.exception.AccessDeniedException;
import com.dentalclinic.patient_tracking.exception.NotFoundException;
import com.dentalclinic.patient_tracking.repository.ClinicRepository;
import com.dentalclinic.patient_tracking.repository.DoctorRepository;
import com.dentalclinic.patient_tracking.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public ClinicDTO addClinic(ClinicDTO dto, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Yeni klinik ekleme yetkiniz yok.");
        }

        Clinic clinic = Clinic.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .isActive(true)
                .build();

        clinicRepository.save(clinic);
        return mapToDTO(clinic);
    }

    @Transactional
    public ClinicDTO updateClinic(Long clinicId, ClinicDTO dto, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Klinik güncelleme yetkiniz yok");
        }

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Klinik bulunamadı"));

        clinic.setName(dto.getName());
        clinic.setAddress(dto.getAddress());
        clinic.setPhoneNumber(dto.getPhoneNumber());
        clinic.setActive(dto.isActive());

        clinicRepository.save(clinic);
        return mapToDTO(clinic);
    }

    public ClinicDTO getClinicById(Long clinicId, User currentUser) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Klinik bulunamadı"));

        Role role = currentUser.getRole();

        switch (role) {
            case ADMIN:
                return mapToDTO(clinic);

            case DOCTOR:
                Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new AccessDeniedException("Doctor bulunamadı"));
                if (!doctor.getClinic().getId().equals(clinicId)) {
                    throw new AccessDeniedException("Bu kliniği görüntüleme yetkiniz yok");
                }
                return mapToDTO(clinic);

            case PATIENT:
                Patient patient = patientRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new AccessDeniedException("Patient bulunamadı"));
                if (patient.getActiveClinic() == null || !patient.getActiveClinic().getId().equals(clinicId)) {
                    throw new AccessDeniedException("Bu kliniği görüntüleme yetkiniz yok");
                }
                return mapToDTO(clinic);

            default:
                throw new AccessDeniedException("Bu kliniği görüntüleme yetkiniz yok");
        }
    }

    public List<ClinicDTO> listClinics(User currentUser) {
        Role role = currentUser.getRole();

        if (role == Role.ADMIN) {
            return clinicRepository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        if (role == Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElse(null);
            if (doctor != null && doctor.getClinic() != null) {
                return List.of(mapToDTO(doctor.getClinic()));
            }
            return List.of();
        }

        if (role == Role.PATIENT) {
            Patient patient = patientRepository.findByUserId(currentUser.getId())
                    .orElse(null);
            if (patient != null && patient.getActiveClinic() != null) {
                return List.of(mapToDTO(patient.getActiveClinic()));
            }
            return List.of();
        }

        return List.of();
    }

    @Transactional
    public void deactivateClinic(Long clinicId, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Klinik silme/pasif etme yetkiniz yok");
        }

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Klinik bulunamadı"));

        clinic.setActive(false);
        clinicRepository.save(clinic);
    }

    private ClinicDTO mapToDTO(Clinic clinic) {
        return ClinicDTO.builder()
                .id(clinic.getId())
                .name(clinic.getName())
                .address(clinic.getAddress())
                .phoneNumber(clinic.getPhoneNumber())
                .isActive(clinic.isActive())
                .createdAt(clinic.getCreatedAt())
                .updatedAt(clinic.getUpdatedAt())
                .build();
    }
}
