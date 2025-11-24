package emino.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import emino.backend.models.MassageForSymptom;
import jakarta.transaction.Transactional;

public interface MassageForSymptomRepository extends JpaRepository<MassageForSymptom, Long> {
    
    @Modifying
    @Transactional
    void deleteByMassageId(Long massageId);
    
    @Modifying
    @Transactional
    void deleteBySymptomId(Long massageId);

    List<MassageForSymptom> findBySymptomId(Long symptomId);

    @Query("""
            SELECT ms FROM MassageForSymptom ms 
            JOIN ms.massage m
            JOIN m.massagesForCompany mfc
            WHERE ms.symptom.id = :symptomId
            AND (:city IS NULL OR mfc.company.city = :city)
            ORDER BY ms.effectivenessScore DESC
    """)
    List<MassageForSymptom> findBSymptomAndCity(@Param("symptomId") Long symptomId, @Param("city") String city);
}