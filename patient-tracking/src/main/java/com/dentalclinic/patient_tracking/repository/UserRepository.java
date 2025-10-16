package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTcNumber(String tcNumber);
}
