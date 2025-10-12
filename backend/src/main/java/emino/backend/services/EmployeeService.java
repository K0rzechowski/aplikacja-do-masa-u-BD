package emino.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.exception.AccessDeniedException;
import emino.backend.models.Admin;
import emino.backend.models.Employee;
import emino.backend.models.User;
import emino.backend.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepository emplRepository;
    
    public List<Employee> getEmployeesByAdmin(Admin admin) {
        return emplRepository.findByCompany(admin.getCompany());
    } 

    public Employee createEmployee(Admin admin, Employee employee) {
        if (!employee.getCompany().equals(admin.getCompany())) {
            throw new AccessDeniedException("Nie można dodać pracownika do innej firmy");
        }
        return emplRepository.save(employee);
    }
}
