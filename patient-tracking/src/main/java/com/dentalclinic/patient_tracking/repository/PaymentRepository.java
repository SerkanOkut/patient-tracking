package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Belirli bir hasta için ödemeleri, ödeme tarihine göre sıralı listele
    List<Payment> findByPatientIdOrderByPaymentDateDesc(Long patientId);

    // Opsiyonel: aktif ödemeleri listeleme
    List<Payment> findByPatientIdAndIsActiveTrueOrderByPaymentDateDesc(Long patientId);

    // Opsiyonel: belirli tarih aralığında ödeme sorgulama
    List<Payment> findByPatientIdAndPaymentDateBetweenOrderByPaymentDateDesc(
            Long patientId, java.time.LocalDate startDate, java.time.LocalDate endDate);
}
