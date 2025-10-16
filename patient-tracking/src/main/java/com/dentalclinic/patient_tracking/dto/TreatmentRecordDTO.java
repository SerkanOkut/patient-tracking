package com.dentalclinic.patient_tracking.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentRecordDTO {
    private Long id;
    private String patientName;
    private String doctorName;
    private String clinicName;
    private String treatmentName;
    private BigDecimal price;
    private LocalDateTime treatmentDate;
}
