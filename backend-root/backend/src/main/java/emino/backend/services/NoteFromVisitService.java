package emino.backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.dto.NoteFromVisitDto;
import emino.backend.models.NoteFromVisit;
import emino.backend.models.Reservation;
import emino.backend.models.User;
import emino.backend.repositories.NoteFromVisitRepository;
import emino.backend.repositories.ReservationRepository;
import emino.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteFromVisitService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRespository;
    private final NoteFromVisitRepository noteRepository;

    public Optional<NoteFromVisit> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    public Optional<NoteFromVisit> getNoteByReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej rezerwacji"));
        return noteRepository.findByReservationId(reservationId);
    }

    public List<NoteFromVisit> getNoteByDateOfCreation(LocalDate dateOfCreation) {
        return noteRepository.findByDateOfCreation(dateOfCreation);
    }

    public List<NoteFromVisit> getCustomerNotes(Long customerId) {
        User user = userRespository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego użytkownika"));
        return noteRepository.findByReservationCustomerId(customerId);
    }

    public NoteFromVisit createNote(NoteFromVisitDto request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej rezerwacji"));

        NoteFromVisit note = NoteFromVisit.builder()
                .reservation(reservation)
                .content(request.getContent())
                .dateOfCreation(LocalDate.now())
                .build();

        return noteRepository.save(note);
    }

    public void removeNoteFromVisit(Long id) {
        noteRepository.deleteById(id);
    }

    public NoteFromVisit editNoteFromVisit(Long id, NoteFromVisitDto request) {
        NoteFromVisit note = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej notatki z wizyty"));

        note.setContent(request.getContent());
        return noteRepository.save(note);
    }

    public NoteFromVisitDto getNoteResponse(NoteFromVisit note) {
        NoteFromVisitDto dto = new NoteFromVisitDto();
        dto.setId(note.getId());
        dto.setReservationId(note.getReservation().getId());
        dto.setContent(note.getContent());
        dto.setDateOfCreation(note.getDateOfCreation());
        return dto;
    }
}