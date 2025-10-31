package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Email ile bulma (login/register)
    Optional<User> findByEmail(String email);

    // Email kullanımda mı kontrolü
    boolean existsByEmail(String email);

    // Opsiyonel: ID ile aktif kullanıcı bulma (JWT kontrolü için)
    Optional<User> findByIdAndIsActiveTrue(Long id);

    // Opsiyonel: role bazlı listeleme
    List<User> findByRole(String role);

    // Opsiyonel: isim veya soyisim ile kısmi arama
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    List<User> findByLastNameContainingIgnoreCase(String lastName);
}
