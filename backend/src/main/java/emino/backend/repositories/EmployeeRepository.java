package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import emino.backend.models.Company;
import emino.backend.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    List<Employee> findByCompany(Company company);
    Optional<Employee> findByEmailAndCompany(String email, Company company);
    Optional<Employee> findByEmail(String email);

    @Query("SELECT DISTINCT e FROM Employee e JOIN e.massagesToHandle m WHERE m.id = :massageId")
    List<Employee> findByMassageId(@Param("massageId") Long massageId);
}