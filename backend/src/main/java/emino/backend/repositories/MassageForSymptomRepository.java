package emino.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import emino.backend.models.MassageForSymptom;
import jakarta.transaction.Transactional;

public interface MassageForSymptomRepository extends JpaRepository<MassageForSymptom, Long> {
    
    @Modifying
    @Transactional
    void deleteByMassageId(Long massageId);
    @Modifying
    @Transactional
    void deleteBySymptomId(Long massageId);
}