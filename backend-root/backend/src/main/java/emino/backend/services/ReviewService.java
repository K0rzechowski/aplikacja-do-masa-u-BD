package emino.backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.dto.ReviewDto;
import emino.backend.exception.AccessDeniedException;
import emino.backend.models.Reservation;
import emino.backend.models.Review;
import emino.backend.models.Status;
import emino.backend.models.User;
import emino.backend.repositories.ReservationRepository;
import emino.backend.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public Optional<Review> getReviewByReservation(Long reservationId) {
        return reviewRepository.findByReservationId(reservationId);
    }

    public List<Review> getReviewsByBranchOffice(Long branchOfficeId) {
        return reviewRepository.findByReservationShiftBranchOfficeId(branchOfficeId);
    }

    public Review createReview(ReviewDto request, User customer) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej rezerwacji"));

        if (!reservation.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("Można ocenić tylko własną wizytę");
        }
        if (reservation.getStatus() != Status.COMPLETED) {
            throw new IllegalStateException("Można ocenić tylko zakończoną wizytę");
        }
        if (reviewRepository.existsByReservationId(reservation.getId())) {
            throw new IllegalStateException("Ta wizyta została już oceniona");
        }
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalStateException("Ocena musi być w zakresie od 1 do 5");
        }

        Review review = Review.builder()
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .dateOfCreation(LocalDate.now())
                .build();

        return reviewRepository.save(review);
    }

    public void removeReview(Long id, User customer) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej opinii"));

        if (!review.getReservation().getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("Można usunąć tylko własną opinię");
        }

        reviewRepository.delete(review);
    }

    public ReviewDto getReviewResponse(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .reservationId(review.getReservation().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .dateOfCreation(review.getDateOfCreation())
                .build();
    }
}
