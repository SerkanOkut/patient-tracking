package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);

    Optional<Doctor> findByPhoneNumber(String phoneNumber);

    List<Doctor> findByFirstNameAndLastName(String firstName, String lastName);

    List<Doctor> findByFirstNameContainingIgnoreCase(String firstName);

    List<Doctor> findByLastNameContainingIgnoreCase(String lastName);
}
