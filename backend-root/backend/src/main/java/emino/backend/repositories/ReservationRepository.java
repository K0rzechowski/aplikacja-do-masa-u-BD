package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByShiftId (Long shiftId);
    List<Reservation> findByShiftAssignedEmployeeId(Long employeeId);
    List<Reservation> findByShiftCompanyId(Long companyId);
    List<Reservation> findByMassageId(Long massageForCompanyId);
    List<Reservation> findByCustomerId(Long customerId);
}