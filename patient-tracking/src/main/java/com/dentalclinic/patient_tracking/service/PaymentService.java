package com.dentalclinic.patient_tracking.service;

import com.dentalclinic.patient_tracking.dto.PaymentDTO;
import com.dentalclinic.patient_tracking.entity.Patient;
import com.dentalclinic.patient_tracking.entity.Payment;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.enums.Role;
import com.dentalclinic.patient_tracking.exception.AccessDeniedException;
import com.dentalclinic.patient_tracking.exception.NotFoundException;
import com.dentalclinic.patient_tracking.repository.PatientRepository;
import com.dentalclinic.patient_tracking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;

    /**
     * Ödeme kaydı ekleme (sadece admin)
     */
    @Transactional
    public PaymentDTO recordPayment(Long patientId, BigDecimal amount, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Ödeme kaydını yalnızca admin ekleyebilir");
        }

        // Hasta entity'sini çekiyoruz
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        // Borç güncelleme
        BigDecimal newDebt = patient.getGeneralDebt().subtract(amount);
        if (newDebt.compareTo(BigDecimal.ZERO) < 0) newDebt = BigDecimal.ZERO;
        patient.setGeneralDebt(newDebt);
        patientRepository.save(patient);

        // Ödeme kaydı oluşturma
        Payment payment = Payment.builder()
                .patient(patient)
                .amount(amount)
                .build();
        paymentRepository.save(payment);

        return mapToDTO(payment);
    }

    /**
     * Belirli bir hastanın ödeme geçmişini alma
     */
    public List<PaymentDTO> getPaymentsForPatient(Long patientId, User currentUser) {
        // Hasta entity'sini çekiyoruz
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        // Rol bazlı erişim kontrolü
        if (currentUser.getRole() == Role.PATIENT &&
                !patientRepository.findByUserId(currentUser.getId())
                        .map(Patient::getId)
                        .orElse(-1L)
                        .equals(patientId)) {
            throw new AccessDeniedException("Başka hastanın ödeme geçmişine erişemezsiniz");
        }

        // Ödeme geçmişini çekip DTO'ya dönüştürme
        return paymentRepository.findByPatientIdOrderByPaymentDateDesc(patientId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Entity → DTO dönüşümü
     */
    private PaymentDTO mapToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .patientId(payment.getPatient().getId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}
