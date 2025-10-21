package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Company;
import emino.backend.models.Employee;
import emino.backend.models.Massage;
import emino.backend.models.Symptom;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    
    Optional<Symptom> findByName(String name);
    List<Symptom> findByCompany(Company company);
    List<Symptom> findByMassage(Massage massage);
    List<Symptom> findByEmployee(Employee employee);
}