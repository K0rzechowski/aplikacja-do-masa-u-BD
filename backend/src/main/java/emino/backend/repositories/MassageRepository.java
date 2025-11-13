package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import emino.backend.models.Company;
import emino.backend.models.Massage;
import emino.backend.models.Symptom;

public interface MassageRepository extends JpaRepository<Massage, Long> {
    
    Optional<Massage> findByName(String name);
    List<Massage> findByCompany(Company company);

    @Query("SELECT m FROM Massage m JOIN m.massageMatchesForSymptom ms WHERE ms.symptom = :symptom")
    List<Massage> findBySymptom(@Param("symptom") Symptom symptom);
    
    @Query("SELECT DISTINCT m FROM Massage m JOIN m.employees e WHERE e.id = :employeeId")
    List<Massage> findByEmployeeId(@Param("employeeId") Long employeeId);
}