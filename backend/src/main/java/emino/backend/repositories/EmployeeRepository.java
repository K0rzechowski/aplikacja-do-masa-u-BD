package emino.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Company;
import emino.backend.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    List<Employee> findByCompany(Company company);
}