package com.dentalclinic.patient_tracking.dto;

import jakarta.persistence.GeneratedValue;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicDTO {

    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
