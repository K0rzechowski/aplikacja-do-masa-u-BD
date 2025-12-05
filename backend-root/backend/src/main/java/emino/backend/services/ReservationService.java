package emino.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.dto.ReservationDto;
import emino.backend.mail.EmailService;
import emino.backend.models.Employee;
import emino.backend.models.MassageForCompany;
import emino.backend.models.Reservation;
import emino.backend.models.Shift;
import emino.backend.models.Status;
import emino.backend.models.User;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.MassageForCompanyRepository;
import emino.backend.repositories.ReservationRepository;
import emino.backend.repositories.ShiftRepository;
import emino.backend.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final EmployeeRepository emplRepository;
    private final CompanyRepository companyRepository;
    private final MassageForCompanyRepository massageRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByCompany(Long companyId) {
        var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));
        return reservationRepository.findByShiftCompanyId(companyId);
    } 

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation createReservation(ReservationDto request) throws MessagingException {
        Reservation reservation = Reservation.builder()
            .customer(request.getCustomer())
            .shift(request.getShift())
            .massage(request.getMassageForCompany())
            .status(Status.RESERVED)
            .build();

        
        Reservation savedReservation = reservationRepository.save(reservation);
        emailService.sendReservationConfirmationEmail(request.getCustomer(), request.getShift().getCompany(), request.getMassageForCompany().getMassage().getName(), request.getShift());
        return savedReservation;
    }

    public void removeReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej rezerwacji"));

        if (!reservation.getCustomer().getId().equals(userId)) {
            throw new IllegalArgumentException("Użytkownik nie ma uprawnień do anulowania tej rezerwacji");
        }

        reservation.setStatus(Status.CANCELLED);
        reservationRepository.save(reservation);
    }

    public void setReservationAsCompleted(Long reservationId, Long employeeId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej rezerwacji"));

        if (!reservation.getShift().getAssignedEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException("Pracownik nie ma uprawnień do oznaczenia tej rezerwacji jako zakończona");
        }

        reservation.setStatus(Status.COMPLETED);
        reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByMassage(Long massageId) {
        MassageForCompany massage = massageRepository.findById(massageId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));
        
        return reservationRepository.findByMassageId(massageId);
    }

    public List<Reservation> getReservationsByCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego klienta"));
        
        return reservationRepository.findByCustomerId(customerId);
    }

    public List<Reservation> getReservationsByEmployee(Long employeeId) {
        Employee employee = emplRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego masażu"));
        
        return reservationRepository.findByShiftAssignedEmployeeId(employeeId);
    }

    public Optional<Reservation> getReservationByShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej zmiany"));

        return reservationRepository.findByShiftId(shiftId);
    }

    public ReservationDto getReservationResponse(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setCustomer(reservation.getCustomer());
        dto.setStatus(reservation.getStatus());
        dto.setShift(reservation.getShift());
        dto.setMassageForCompany(reservation.getMassage());
        return dto;
    }
}