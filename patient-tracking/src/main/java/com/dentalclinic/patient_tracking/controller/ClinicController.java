package com.dentalclinic.patient_tracking.controller;


import com.dentalclinic.patient_tracking.dto.ClinicDTO;
import com.dentalclinic.patient_tracking.entity.Patient;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;

    @PostMapping
    public ResponseEntity<ClinicDTO> addClinic(@RequestBody ClinicDTO dto,
                                               @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(clinicService.addClinic(dto, currentUser));
    }

    @PutMapping("/{clinicId}")
    public ResponseEntity<ClinicDTO> updateClinic(@PathVariable Long clinicId,
                                                  @RequestBody ClinicDTO dto,
                                                  @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(clinicService.updateClinic(clinicId, dto, currentUser));
    }

    @GetMapping("/{clinicId}")
    public ResponseEntity<ClinicDTO> getClinic(@PathVariable Long clinicId,
                                               @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(clinicService.getClinicById(clinicId, currentUser));
    }

    @GetMapping
    public  ResponseEntity<List<ClinicDTO>> listClinics(@AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(clinicService.listClinics(currentUser));
    }

    @DeleteMapping("/{clinicId}")
    public ResponseEntity<Void> deactiveClinic(@PathVariable Long clinicId,
                                               @AuthenticationPrincipal User currentUser) {
        clinicService.deactivateClinic(clinicId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
