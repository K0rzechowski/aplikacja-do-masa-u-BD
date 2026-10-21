package emino.backend.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    
    List<Shift> findByBranchOfficeIdAndStartTimeBetween(Long branchOfficeId, LocalDateTime startTime, LocalDateTime endTime);
    List<Shift> findByBranchOfficeCompanyIdAndStartTimeBetween(Long companyId, LocalDateTime startTime, LocalDateTime endTime);
    List<Shift> findByAssignedEmployeeIdAndStartTimeBetween(Long employeeId, LocalDateTime startTime, LocalDateTime endTime);
    boolean existsByAssignedEmployeeIdAndStartTimeLessThanAndEndTimeGreaterThan(Long employeeId, LocalDateTime endTime, LocalDateTime startTime);
    boolean existsByRoomIdAndStartTime(Long roomId, LocalDateTime startTime);
    List<Shift> findByReservationIsNull();
}
