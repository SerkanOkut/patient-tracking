package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.TreatmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRecordRepository  extends JpaRepository<TreatmentRecord, Long> {
    List<TreatmentRecord> findByPatientId(Long patientId);
    List<TreatmentRecord> findByDoctorId(Long doctorId);
    List<TreatmentRecord> findByClinicId(Long clinicId);
    List<TreatmentRecord> findByTreatmentNameContainingIgnoreCase( String treatmentName);
}
