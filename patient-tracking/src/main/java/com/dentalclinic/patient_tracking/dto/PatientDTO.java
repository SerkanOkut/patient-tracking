package com.dentalclinic.patient_tracking.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String tcNumber;
    private String phoneNumber;
    private BigDecimal generalDebt;
    private String activeClinicName;

}
