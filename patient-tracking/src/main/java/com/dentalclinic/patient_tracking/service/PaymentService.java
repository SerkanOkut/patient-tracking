package com.dentalclinic.patient_tracking.service;


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

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;


    @Transactional
    public void  recordPayment(Long patientId, BigDecimal amount, User currentUser) {

        if(currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Ödeme kaydını yalnızca admin ekleyebilir");
        }
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        BigDecimal newDebt = patient.getGeneralDebt().subtract(amount);
        if(newDebt.compareTo(BigDecimal.ZERO)<0) newDebt = BigDecimal.ZERO;

        BigDecimal debtChange = newDebt.subtract(patient.getGeneralDebt());
        patientService.updateGeneralDebt(patientId,debtChange);

        Payment payment = Payment.builder()
                .patient(patient)
                .amount(amount)
                .build();
        paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsForPatient(Long patientId, User currentUser) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Hasta bulunamadı"));

        if(currentUser.getRole() == Role.PATIENT &&
        !currentUser.getPatient().getId().equals(patientId)) {
            throw new AccessDeniedException("Başka hastanın ödeme geçmişine erişemezsiniz");
        }
        return paymentRepository.findByPatientIdOrderByPaymentDateDesc(patientId);
    }
}
