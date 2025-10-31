package com.dentalclinic.patient_tracking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long clinicId;
    private String phoneNumber;
    private String clinicName;
    private  boolean isActive;
}
