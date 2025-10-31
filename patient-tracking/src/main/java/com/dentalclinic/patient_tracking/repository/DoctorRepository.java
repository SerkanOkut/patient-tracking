package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Email ile bulma (login/register için)
    Optional<Doctor> findByEmail(String email);

    // Telefon numarası ile bulma
    Optional<Doctor> findByPhoneNumber(String phoneNumber);


    Optional<Doctor> findByUserId(Long id);
    // İsim ve soyisim ile bulma
    List<Doctor> findByFirstNameAndLastName(String firstName, String lastName);

    // İsim ile kısmi arama (ignore case)
    List<Doctor> findByFirstNameContainingIgnoreCase(String firstName);

    // Soyisim ile kısmi arama (ignore case)
    List<Doctor> findByLastNameContainingIgnoreCase(String lastName);

    // Opsiyonel: email ile aktif doktor bulma (JWT kontrolü için)
    Optional<Doctor> findByEmailAndIsActiveTrue(String email);

    // Opsiyonel: ID ile aktif doktor bulma
    Optional<Doctor> findByIdAndIsActiveTrue(Long id);

    // Opsiyonel: rol veya klinik bazlı filtreleme yapılacaksa eklenebilir
}
