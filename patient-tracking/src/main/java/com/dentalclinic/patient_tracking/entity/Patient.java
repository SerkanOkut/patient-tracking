package com.dentalclinic.patient_tracking.entity;

import com.dentalclinic.patient_tracking.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true , length = 11)
    private String tcNumber;

    private LocalDate birthDate;

    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false , precision = 10, scale = 2)
    private BigDecimal generalDebt = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "active_clinic_id")
    private Clinic activeClinic;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private  LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
