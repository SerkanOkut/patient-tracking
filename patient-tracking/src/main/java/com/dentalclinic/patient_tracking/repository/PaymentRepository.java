package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


    List<Payment> findByPatientIdOrderByPaymentDateDesc(Long patientId);

}
