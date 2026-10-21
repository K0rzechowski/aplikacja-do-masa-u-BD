package emino.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.config.SecurityService;
import emino.backend.dto.ReviewDto;
import emino.backend.models.Review;
import emino.backend.services.ReviewService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityService securityService;

    @PostMapping("/create")
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto request) {
        Review review = reviewService.createReview(request, securityService.getCurrentUser());
        return ResponseEntity.ok(reviewService.getReviewResponse(review));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(review -> ResponseEntity.ok(reviewService.getReviewResponse(review)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ReviewDto> getByReservation(@PathVariable Long reservationId) {
        return reviewService.getReviewByReservation(reservationId)
                .map(review -> ResponseEntity.ok(reviewService.getReviewResponse(review)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<ReviewDto>> getByBranchOffice(@PathVariable Long branchId) {
        List<ReviewDto> dto = reviewService.getReviewsByBranchOffice(branchId).stream()
                .sorted((r1, r2) -> r2.getDateOfCreation().compareTo(r1.getDateOfCreation()))
                .map(reviewService::getReviewResponse)
                .toList();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeReview(@PathVariable Long id) {
        reviewService.removeReview(id, securityService.getCurrentUser());
        return ResponseEntity.ok().build();
    }
}
