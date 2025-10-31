package com.dentalclinic.patient_tracking.controller;


import com.dentalclinic.patient_tracking.dto.PatientDTO;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private  final PatientService patientService;

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDTO> getPatient(@PathVariable Long patientId,
                                                 @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(patientService.getPatientById(patientId,currentUser));
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(patientService.getAllPatients(currentUser));
    }
}
