package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByTcNumber(String tcNumber);

    Optional<Patient> findByEmail (String email);

    Optional<Patient> findByPhoneNumber(String phoneNumber);

}
