package emino.backend.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.NoteFromVisit;

public interface NoteFromVisitRepository extends JpaRepository<NoteFromVisit, Long> {
    Optional<NoteFromVisit> findByReservationId(Long reservationId);

    List<NoteFromVisit> findByReservationCustomerId(Long customerId);

    List<NoteFromVisit> findByDateOfCreation(LocalDate dateOfCreation);
}