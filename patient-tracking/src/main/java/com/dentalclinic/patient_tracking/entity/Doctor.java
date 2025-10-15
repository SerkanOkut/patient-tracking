package com.dentalclinic.patient_tracking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="doctors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false , length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true , length = 15)
    private String phoneNumber;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="clinic_id")
    private Clinic clinic;

    @Column(nullable = false)
    private boolean isActive= true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected  void  onCreate(){
    createdAt = LocalDateTime.now();
    updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt= LocalDateTime.now();
    }
}
