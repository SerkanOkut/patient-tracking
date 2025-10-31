package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // TC Kimlik numarası ile bulma (unique)
    Optional<Patient> findByTcNumber(String tcNumber);

    // Email ile bulma (login/register)
    Optional<Patient> findByEmail(String email);

    // Telefon numarası ile bulma
    Optional<Patient> findByPhoneNumber(String phoneNumber);

    Optional<Patient> findByUserId(Long id);
    // Opsiyonel: ID ile aktif hasta bulma (JWT kontrolü için)


    // Opsiyonel: isme göre kısmi arama
    List<Patient> findByFirstNameContainingIgnoreCase(String firstName);

    // Opsiyonel: soyisme göre kısmi arama
    List<Patient> findByLastNameContainingIgnoreCase(String lastName);


}
