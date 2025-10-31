package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    // İsimle bulma (register/update işlemleri veya kontrol için)
    Optional<Clinic> findByName(String name);

    // Aktif klinikleri listeleme
    List<Clinic> findAllByIsActiveTrue();

    // Opsiyonel: ID ile aktif klinik bulma (JWT ile rol bazlı kontrol için)
    Optional<Clinic> findByIdAndIsActiveTrue(Long id);

    // Opsiyonel: birden fazla kriter ile sorgulama yapılabilir
    List<Clinic> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}
