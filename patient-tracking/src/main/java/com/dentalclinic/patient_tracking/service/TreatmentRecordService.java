package com.dentalclinic.patient_tracking.service;

import com.dentalclinic.patient_tracking.dto.TreatmentRecordDTO;
import com.dentalclinic.patient_tracking.entity.Doctor;
import com.dentalclinic.patient_tracking.entity.Patient;
import com.dentalclinic.patient_tracking.entity.TreatmentRecord;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.enums.Role;
import com.dentalclinic.patient_tracking.exception.AccessDeniedException;
import com.dentalclinic.patient_tracking.exception.NotFoundException;
import com.dentalclinic.patient_tracking.repository.PatientRepository;
import com.dentalclinic.patient_tracking.repository.TreatmentRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreatmentRecordService {

    private final TreatmentRecordRepository treatmentRecordRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    /**
     * Yeni tedavi ekleme ve borç otomatik güncelleme
     */
    @Transactional
    public TreatmentRecordDTO addTreatment(Long patientId, TreatmentRecordDTO dto, User currentUser) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        Doctor doctor = doctorService.getDoctorByUserId(currentUser.getId()); // JWT userId üzerinden doctor çek

        // Rol bazlı erişim kontrolü
        if (currentUser.getRole() == Role.DOCTOR) {
            boolean hasAccess = treatmentRecordRepository.existsByPatientIdAndDoctorId(patientId, doctor.getId());
            if (!hasAccess) {
                throw new AccessDeniedException("Bu hastaya tedavi ekleme yetkiniz yok");
            }
        } else if (currentUser.getRole() == Role.PATIENT) {
            throw new AccessDeniedException("Hasta kendi tedavisini ekleyemez");
        }

        TreatmentRecord treatment = TreatmentRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .clinic(patient.getActiveClinic())
                .treatmentName(dto.getTreatmentName())
                .price(dto.getPrice())
                .treatmentDate(dto.getTreatmentDate() != null ? dto.getTreatmentDate() : LocalDateTime.now())
                .build();

        treatmentRecordRepository.save(treatment);

        // Borç otomatik güncelleme
        patientService.updateGeneralDebt(patientId, dto.getPrice());

        return mapToDTO(treatment);
    }

    /**
     * Hasta veya rol bazlı tedavi kayıtlarını listeleme
     */
    public List<TreatmentRecordDTO> getTreatmentsForPatient(Long patientId, User currentUser) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        // Rol bazlı kontrol
        if (currentUser.getRole() == Role.DOCTOR) {
            Doctor doctor = doctorService.getDoctorByUserId(currentUser.getId());
            boolean hasAccess = treatmentRecordRepository.existsByPatientIdAndDoctorId(patientId, doctor.getId());
            if (!hasAccess) throw new AccessDeniedException("Bu hastaya erişim yetkiniz yok");
        } else if (currentUser.getRole() == Role.PATIENT) {
            Patient currentPatient = patientService.getPatientByUserId(currentUser.getId());
            if (!currentPatient.getId().equals(patientId)) {
                throw new AccessDeniedException("Bu hastaya erişim yetkiniz yok");
            }
        }

        return treatmentRecordRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Entity → DTO dönüşümü
     */
    private TreatmentRecordDTO mapToDTO(TreatmentRecord tr) {
        return TreatmentRecordDTO.builder()
                .id(tr.getId())
                .patientName(tr.getPatient().getFirstName() + " " + tr.getPatient().getLastName())
                .doctorName(tr.getDoctor().getFirstName() + " " + tr.getDoctor().getLastName())
                .clinicName(tr.getClinic().getName())
                .treatmentName(tr.getTreatmentName())
                .price(tr.getPrice())
                .treatmentDate(tr.getTreatmentDate())
                .build();
    }

    /**
     * Kısmi ödeme yapma ve borç düşme
     */
    @Transactional
    public void makePayment(Long patientId, BigDecimal paymentAmount, User currentUser) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        // Rol kontrolü
        if (currentUser.getRole() == Role.DOCTOR) {
            throw new AccessDeniedException("Doktor ödeme gerçekleştiremez");
        } else if (currentUser.getRole() == Role.PATIENT) {
            Patient currentPatient = patientService.getPatientByUserId(currentUser.getId());
            if (!currentPatient.getId().equals(patientId)) {
                throw new AccessDeniedException("Başka hastanın borcunu ödeyemezsiniz");
            }
        }

        BigDecimal newDebt = patient.getGeneralDebt().subtract(paymentAmount);
        if (newDebt.compareTo(BigDecimal.ZERO) < 0) newDebt = BigDecimal.ZERO;

        patientService.updateGeneralDebt(patientId, newDebt.subtract(patient.getGeneralDebt()));
    }
}
