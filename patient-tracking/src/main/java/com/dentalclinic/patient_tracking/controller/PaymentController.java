package com.dentalclinic.patient_tracking.controller;


import com.dentalclinic.patient_tracking.dto.PaymentDTO;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

private final PaymentService paymentService ;

@PostMapping("/{patientId}")
    public ResponseEntity<PaymentDTO> addPayment(@PathVariable Long patientId,
                                                  @RequestParam BigDecimal amount,
                                                  @AuthenticationPrincipal User currentUser) {
    return ResponseEntity.ok(paymentService.recordPayment(patientId, amount, currentUser));
}

@GetMapping("/{patientId}")
public ResponseEntity<List<PaymentDTO>> getPayments(@PathVariable Long patientId,
                                                    @AuthenticationPrincipal User currentUser) {
    return ResponseEntity.ok(paymentService.getPaymentsForPatient(patientId,currentUser));
    }

}
