package emino.backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.config.SecurityService;
import emino.backend.dto.ReservationDto;
import emino.backend.models.Reservation;
import emino.backend.services.ReservationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ReservationController {
    
    private final ReservationService reservationService;
    private final SecurityService securityService;

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ReservationDto>> getReservationsById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        Optional<ReservationDto> reservationDto = reservation.map(reservationService::getReservationResponse);
        return ResponseEntity.ok(reservationDto);
    }

    @PostMapping("/create")
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationDto request) throws MessagingException {
        Reservation reservation = reservationService.createReservation(request);
        ReservationDto dto = reservationService.getReservationResponse(reservation);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<List<ReservationDto>> getReservationsByCompany(@PathVariable Long id) {
        List<Reservation> reservations = reservationService.getReservationsByCompany(id);
        List<ReservationDto> reservationDtos = reservations.stream()
            .map(reservationService::getReservationResponse)
            .sorted((r1, r2) -> r1.getShift().getStartTime().compareTo(r2.getShift().getStartTime()))
            .toList();
        return ResponseEntity.ok(reservationDtos);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity removeReservation(@PathVariable Long id) {
        reservationService.removeReservation(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) throws MessagingException {
        Long userId = securityService.getCurrentUserId();
        reservationService.cancelReservation(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/complete/{id}")
    public ResponseEntity<Void> setReservationAsCompleted(@PathVariable Long id) {
        Long employeeId = securityService.getCurrentUserId();
        reservationService.setReservationAsCompleted(id, employeeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/massage/{id}")
    public ResponseEntity<List<ReservationDto>> getReservationsByMassage(@PathVariable Long id) {
        List<Reservation> reservations = reservationService.getReservationsByMassage(id);
        List<ReservationDto> reservationDtos = reservations.stream()
            .map(reservationService::getReservationResponse)
            .sorted((r1, r2) -> r1.getShift().getStartTime().compareTo(r2.getShift().getStartTime()))
            .toList();
        return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<ReservationDto>> getReservationsByCustomer(@PathVariable Long id) {
        List<Reservation> reservations = reservationService.getReservationsByCustomer(id);
        List<ReservationDto> reservationDtos = reservations.stream()
            .map(reservationService::getReservationResponse)
            .sorted((r1, r2) -> r1.getShift().getStartTime().compareTo(r2.getShift().getStartTime()))
            .toList();
        return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<ReservationDto>> getReservationsByEmployee(@PathVariable Long id) {
        List<Reservation> reservations = reservationService.getReservationsByEmployee(id);
        List<ReservationDto> reservationDtos = reservations.stream()
            .map(reservationService::getReservationResponse)
            .sorted((r1, r2) -> r1.getShift().getStartTime().compareTo(r2.getShift().getStartTime()))
            .toList();
        return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/shift/{id}")
    public ResponseEntity<Optional<ReservationDto>> getReservationsByShift(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationByShift(id);
        Optional<ReservationDto> reservationDto = reservation.map(reservationService::getReservationResponse);
        return ResponseEntity.ok(reservationDto);
    }

}
