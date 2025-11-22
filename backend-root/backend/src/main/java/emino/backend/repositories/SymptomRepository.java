package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import emino.backend.models.Massage;
import emino.backend.models.Symptom;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    
    Optional<Symptom> findByName(String name);
    @Query("SELECT s FROM Symptom s JOIN s.massageMatchesForSymptom sm WHERE sm.massage = :massage")
    List<Symptom> findByMassage(@Param("massage") Massage massage);
}