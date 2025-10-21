package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Company;
import emino.backend.models.Employee;
import emino.backend.models.Massage;
import emino.backend.models.Symptom;

public interface MassageRepository extends JpaRepository<Massage, Long> {
    
    Optional<Massage> findByName(String name);
    List<Massage> findByCompany(Company company);
    List<Massage> findBySymptom(Symptom symptom);
    List<Massage> findByEmployee(Employee employee);
}