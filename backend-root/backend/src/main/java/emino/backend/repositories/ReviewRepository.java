package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReservationId(Long reservationId);

    List<Review> findByReservationShiftBranchOfficeId(Long branchOfficeId);

    boolean existsByReservationId(Long reservationId);
}
