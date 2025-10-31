package com.dentalclinic.patient_tracking.service;

import com.dentalclinic.patient_tracking.dto.DoctorDTO;
import com.dentalclinic.patient_tracking.entity.Clinic;
import com.dentalclinic.patient_tracking.entity.Doctor;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.enums.Role;
import com.dentalclinic.patient_tracking.exception.AccessDeniedException;
import com.dentalclinic.patient_tracking.exception.NotFoundException;
import com.dentalclinic.patient_tracking.repository.ClinicRepository;
import com.dentalclinic.patient_tracking.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;

    /**
     * Yeni doktor kaydı oluşturma (sadece admin yapabilir)
     */
    @Transactional
    public DoctorDTO createDoctor(DoctorDTO dto, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Doktor kaydı oluşturma yetkiniz yok.");
        }

        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new NotFoundException("Klinik bulunamadı."));

        Doctor doctor = Doctor.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .clinic(clinic)
                .isActive(true)
                .build();

        doctorRepository.save(doctor);
        return mapToDTO(doctor);
    }

    /**
     * Doktor bilgilerini güncelleme (admin veya ilgili doktor)
     */
    @Transactional
    public DoctorDTO updateDoctor(Long doctorId, DoctorDTO dto, User currentUser) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doktor bulunamadı."));

        // Yetki kontrolü
        if (currentUser.getRole() == Role.DOCTOR) {
            Doctor currentDoctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new AccessDeniedException("Kendi profiliniz dışında erişim yok."));
            if (!currentDoctor.getId().equals(doctorId)) {
                throw new AccessDeniedException("Kendi profiliniz dışında erişim yok.");
            }
        } else if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Bu doktorun bilgilerini güncelleme yetkiniz yok.");
        }

        if (dto.getFirstName() != null) doctor.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) doctor.setLastName(dto.getLastName());
        if (dto.getEmail() != null) doctor.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) doctor.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getClinicId() != null) {
            Clinic clinic = clinicRepository.findById(dto.getClinicId())
                    .orElseThrow(() -> new NotFoundException("Klinik bulunamadı."));
            doctor.setClinic(clinic);
        }

        doctorRepository.save(doctor);
        return mapToDTO(doctor);
    }

    /**
     * Doktoru pasif hale getirme (sadece admin)
     */
    @Transactional
    public void deactivateDoctor(Long doctorId, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Doktor pasif etme yetkiniz yok.");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doktor bulunamadı."));

        doctor.setActive(false);
        doctorRepository.save(doctor);
    }
    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Doctor bulunamadı"));
    }
    /**
     * Tüm doktorları listeleme (sadece admin)
     */
    public List<DoctorDTO> getAllDoctors(User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Tüm doktor kayıtlarını görüntüleme yetkiniz yok.");
        }

        return doctorRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Doktorun kendi profilini görüntüleme
     */
    public DoctorDTO getDoctorProfile(Long doctorId, User currentUser) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doktor bulunamadı."));

        if (currentUser.getRole() == Role.DOCTOR) {
            Doctor currentDoctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new AccessDeniedException("Kendi profiliniz dışında erişim yok."));
            if (!currentDoctor.getId().equals(doctorId)) {
                throw new AccessDeniedException("Kendi profiliniz dışında erişim yok.");
            }
        }

        if (currentUser.getRole() == Role.PATIENT) {
            throw new AccessDeniedException("Hasta doktor profillerine erişemez.");
        }

        return mapToDTO(doctor);
    }

    /**
     * Entity → DTO dönüşümü
     */
    private DoctorDTO mapToDTO(Doctor doctor) {
        return DoctorDTO.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .email(doctor.getEmail())
                .phoneNumber(doctor.getPhoneNumber())
                .clinicId(doctor.getClinic() != null ? doctor.getClinic().getId() : null)
                .clinicName(doctor.getClinic() != null ? doctor.getClinic().getName() : null)
                .isActive(doctor.isActive())
                .build();
    }
}
