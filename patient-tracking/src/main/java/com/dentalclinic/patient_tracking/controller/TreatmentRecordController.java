package com.dentalclinic.patient_tracking.controller;


import com.dentalclinic.patient_tracking.dto.TreatmentRecordDTO;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.service.TreatmentRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/treatments")
@RequiredArgsConstructor
public class TreatmentRecordController {

    private final TreatmentRecordService treatmentRecordService;

    @PostMapping("/{patientId}")
    public ResponseEntity<TreatmentRecordDTO> addTreatment(@PathVariable Long patientId,
                                                           @RequestBody TreatmentRecordDTO dto,
                                                           @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(treatmentRecordService.addTreatment(patientId,dto,currentUser));
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<List<TreatmentRecordDTO>> getTreatments(@PathVariable Long patientId,
                                                                  @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(treatmentRecordService.getTreatmentsForPatient(patientId,currentUser));

    }

    @PostMapping("/{patientId}/pay")
    public ResponseEntity<Void> makePayment(@PathVariable Long patientId,
                                            @RequestParam BigDecimal amount,
                                            @AuthenticationPrincipal User currentUser) {
        treatmentRecordService.makePayment(patientId, amount, currentUser);
        return ResponseEntity.noContent().build();
    }
}
