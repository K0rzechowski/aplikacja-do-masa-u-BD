package emino.backend.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.EmployeeAvailability;

public interface EmployeeAvailabilityRepository extends JpaRepository<EmployeeAvailability, Long> {

    List<EmployeeAvailability> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    List<EmployeeAvailability> findByDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<EmployeeAvailability> findByEmployeeIdAndDate(Long employeeId, LocalDate startDate);

    List<EmployeeAvailability> findByEmployeeCompanyIdAndDateBetween(Long companyId, LocalDate startDate, LocalDate endDate);
}