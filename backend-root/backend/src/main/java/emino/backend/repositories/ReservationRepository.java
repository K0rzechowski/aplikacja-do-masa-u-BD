package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Reservation;
import emino.backend.models.Status;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByShiftId (Long shiftId);
    List<Reservation> findByShiftAssignedEmployeeId(Long employeeId);
    List<Reservation> findByShiftBranchOfficeCompanyId(Long companyId);
    List<Reservation> findByMassageId(Long massageForCompanyId);
    List<Reservation> findByCustomerId(Long customerId);
    Optional<Reservation> findByStatusAndShiftId(Status status, Long shiftId);
}