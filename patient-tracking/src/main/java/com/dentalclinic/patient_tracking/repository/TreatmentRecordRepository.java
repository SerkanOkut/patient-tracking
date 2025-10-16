package com.dentalclinic.patient_tracking.repository;

import com.dentalclinic.patient_tracking.entity.Patient;
import com.dentalclinic.patient_tracking.entity.TreatmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRecordRepository  extends JpaRepository<TreatmentRecord, Long> {

    List<TreatmentRecord> findByPatientId(Long patientId);

    List<TreatmentRecord> findByDoctorId(Long doctorId);

    List<TreatmentRecord> findByClinicId(Long clinicId);

    List<TreatmentRecord> findByTreatmentNameContainingIgnoreCase( String treatmentName);

    @Query("SELECT DISTINCT tr.patient FROM TreatmentRecord tr WHERE tr.doctor.id = :doctorId")
    List<Patient> findPatientsByDoctorId(@Param("doctorId") Long doctorId);
    @Query("SELECT CASE WHEN COUNT(tr) > 0 THEN true ELSE false END " +
            "FROM TreatmentRecord tr " +
            "WHERE tr.patient.id = :patientId AND tr.doctor.id = :doctorId")
    boolean existsByPatientIdAndDoctorId(@Param("patientId") Long patientId, @Param("doctorId") Long doctorId);

}
