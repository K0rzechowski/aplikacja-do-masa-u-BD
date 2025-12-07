package emino.backend.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.config.SecurityService;
import emino.backend.dto.NoteFromVisitDto;
import emino.backend.models.NoteFromVisit;
import emino.backend.services.CompanyService;
import emino.backend.services.NoteFromVisitService;
import emino.backend.services.ReservationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class NoteFromVisitController {

    private final NoteFromVisitService noteService;
    private final ReservationService reservationService;
    private final CompanyService companyService;
    private final SecurityService securityService;

    @PostMapping("/create")
    public ResponseEntity<NoteFromVisitDto> createNote(@RequestBody NoteFromVisitDto request) {
        NoteFromVisit note = noteService.createNote(request);
        NoteFromVisitDto dto = noteService.getNoteResponse(note);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteFromVisitDto> getById(@PathVariable Long id) {
        NoteFromVisit note = noteService.getNoteById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej notatki"));

        NoteFromVisitDto dto = noteService.getNoteResponse(note);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity removeNote(@PathVariable Long id) {
        noteService.removeNoteFromVisit(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NoteFromVisitDto> updateNote(@PathVariable Long id, @RequestBody NoteFromVisitDto request) {
        NoteFromVisit updatedNote = noteService.editNoteFromVisit(id, request);
        NoteFromVisitDto dto = noteService.getNoteResponse(updatedNote);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<NoteFromVisitDto> getByReservation(@PathVariable Long reservationId) {
        NoteFromVisit note = noteService.getNoteByReservation(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono notatki dla danej rezerwacji"));
        NoteFromVisitDto dto = noteService.getNoteResponse(note);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/date")
    public ResponseEntity<List<NoteFromVisitDto>> getByReservation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfCreation) {
        List<NoteFromVisit> notes = noteService.getNoteByDateOfCreation(dateOfCreation);
        List<NoteFromVisitDto> dto = notes.stream()
                .sorted((n1, n2) -> n2.getDateOfCreation().compareTo(n1.getDateOfCreation()))
                .map(noteService::getNoteResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<NoteFromVisitDto>> getByCustomer(@PathVariable Long customerId) {
        List<NoteFromVisit> notes = noteService.getCustomerNotes(customerId);
        List<NoteFromVisitDto> dto = notes.stream()
                .sorted((n1, n2) -> n2.getDateOfCreation().compareTo(n1.getDateOfCreation()))
                .map(noteService::getNoteResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

}
