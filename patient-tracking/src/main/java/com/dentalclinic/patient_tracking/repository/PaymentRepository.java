package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Belirli bir hasta için ödemeleri, ödeme tarihine göre sıralı listele
    List<Payment> findByPatientIdOrderByPaymentDateDesc(Long patientId);

    // Belirli tarih aralığında ödeme sorgulama
    List<Payment> findByPatientIdAndPaymentDateBetweenOrderByPaymentDateDesc(
            Long patientId, LocalDate startDate, LocalDate endDate);
}
