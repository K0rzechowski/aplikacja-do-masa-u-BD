package emino.backend.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    
    List<Shift> findByCompanyIdAndStartTimeBetween(Long companyId, LocalDateTime startTime, LocalDateTime endTime);
    List<Shift> findByReservationIsNull();
}