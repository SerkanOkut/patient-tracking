package com.dentalclinic.patient_tracking.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;
    private Long patientId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;

}
