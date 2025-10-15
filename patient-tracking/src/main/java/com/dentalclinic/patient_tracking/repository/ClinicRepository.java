package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic , Long> {

    Optional<Clinic> findByName(String name);

    java.util.List<Clinic> findAllByIsActiveTrue();
}
