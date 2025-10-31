package com.dentalclinic.patient_tracking.controller;


import com.dentalclinic.patient_tracking.dto.DoctorDTO;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorDTO dto,
                                                  @AuthenticationPrincipal User currentUser) {
return ResponseEntity.ok(doctorService.createDoctor(dto, currentUser));
    }

    @PutMapping
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Long doctorId,
                                                  @RequestBody DoctorDTO dto,
                                                  @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(doctorService.updateDoctor(doctorId, dto , currentUser));
    }

    @DeleteMapping ("/{doctorId}")
    public ResponseEntity<Void> deactiveDoctor(@PathVariable Long doctorId,
                                               @AuthenticationPrincipal User currentUser) {
        doctorService.deactivateDoctor(doctorId, currentUser);
        return  ResponseEntity.noContent().build();
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDTO> getDoctor(@PathVariable Long doctorId,
                                               @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(doctorService.getDoctorProfile(doctorId, currentUser));
    }
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors (@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(doctorService.getAllDoctors(currentUser));
    }
}
