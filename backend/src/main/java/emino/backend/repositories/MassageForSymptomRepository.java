package emino.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.MassageForSymptom;

public interface MassageForSymptomRepository extends JpaRepository<MassageForSymptom, Long> {
    
    void deleteByMassageId(Long massageId);
    void deleteBySymptomId(Long massageId);
}