package emino.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import emino.backend.dto.ShiftResponseDto;
import emino.backend.exception.AccessDeniedException;
import emino.backend.models.BranchOffice;
import emino.backend.models.CompanyOpenTime;
import emino.backend.models.Employee;
import emino.backend.models.Room;
import emino.backend.models.Shift;
import emino.backend.models.Status;
import emino.backend.repositories.CompanyOpenTimeRepository;
import emino.backend.repositories.ReservationRepository;
import emino.backend.repositories.RoomRepository;
import emino.backend.repositories.ShiftRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final CompanyOpenTimeRepository openTimeRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public Optional<Shift> getShiftById(Long shiftId) {
        return shiftRepository.findById(shiftId);
    }

    public int generateShiftsForNextWeek(BranchOffice branchOffice, LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        CompanyOpenTime openTime = openTimeRepository.findByBranchOfficeId(branchOffice.getId())
            .orElseThrow(() -> new IllegalStateException("Placówka nie ma ustalonych godzin otwarcia"));
        int dbOpenTime = openTime.getOpenTime().getHour();
        int dbCloseTime = openTime.getCloseTime().getHour();

        List<Room> rooms = roomRepository.findByBranchOfficeId(branchOffice.getId());
        if (rooms.isEmpty()) {
            throw new IllegalStateException("Placówka nie ma żadnych pokoi");
        }

        List<Shift> shifts = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            for (int hour = dbOpenTime; hour < dbCloseTime; hour++) {
                LocalDateTime start = date.atTime(hour, 0);
                LocalDateTime end = start.plusMinutes(55);

                for (Room room : rooms) {
                    if (shiftRepository.existsByRoomIdAndStartTime(room.getId(), start)) {
                        continue;
                    }

                    Shift shift = Shift.builder()
                        .startTime(start)
                        .endTime(end)
                        .room(room)
                        .branchOffice(branchOffice)
                        .assignedEmployee(null)
                        .build();

                    shifts.add(shift);
                }
            }
        }
        shiftRepository.saveAll(shifts);
        return shifts.size();
    }

    @Transactional
    public Shift signUp(Long shiftId, Employee employee) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej zmiany"));

        if (!shift.getBranchOffice().getId().equals(employee.getBranchOffice().getId())) {
            throw new AccessDeniedException("Zmiana należy do innej placówki");
        }
        if (shift.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Nie można zapisać się na zmianę, która już się rozpoczęła");
        }
        if (shift.getAssignedEmployee() != null) {
            throw new IllegalStateException("Na tę zmianę zapisał się już inny pracownik");
        }
        if (shiftRepository.existsByAssignedEmployeeIdAndStartTimeLessThanAndEndTimeGreaterThan(
                employee.getId(), shift.getEndTime(), shift.getStartTime())) {
            throw new IllegalStateException("Pracownik ma już zmianę w tym czasie");
        }

        shift.setAssignedEmployee(employee);
        return shiftRepository.save(shift);
    }

    @Transactional
    public Shift signOff(Long shiftId, Employee employee) {
        Shift shift = shiftRepository.findById(shiftId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej zmiany"));

        if (shift.getAssignedEmployee() == null || !shift.getAssignedEmployee().getId().equals(employee.getId())) {
            throw new AccessDeniedException("Pracownik nie jest zapisany na tę zmianę");
        }
        if (reservationRepository.findByStatusAndShiftId(Status.RESERVED, shiftId).isPresent()) {
            throw new IllegalStateException("Nie można wypisać się ze zmiany, na którą jest rezerwacja");
        }

        shift.setAssignedEmployee(null);
        return shiftRepository.save(shift);
    }

    public List<Shift> getShiftsByCompany(Long companyId, LocalDateTime startTime, LocalDateTime endTime) {
        return shiftRepository.findByBranchOfficeCompanyIdAndStartTimeBetween(companyId, startTime, endTime);
    }

    public List<Shift> getShiftsByBranchOffice(Long branchOfficeId, LocalDateTime startTime, LocalDateTime endTime) {
        return shiftRepository.findByBranchOfficeIdAndStartTimeBetween(branchOfficeId, startTime, endTime);
    }

    public List<Shift> getShiftsByEmployee(Long employeeId, LocalDateTime startTime, LocalDateTime endTime) {
        return shiftRepository.findByAssignedEmployeeIdAndStartTimeBetween(employeeId, startTime, endTime);
    }

    public ShiftResponseDto getShiftResponse(Shift shift) {
        ShiftResponseDto dto = ShiftResponseDto.builder()
            .id(shift.getId())
            .companyId(shift.getBranchOffice().getCompany().getId())
            .branchOfficeId(shift.getBranchOffice().getId())
            .branchOfficeName(shift.getBranchOffice().getName())
            .startTime(shift.getStartTime())
            .endTime(shift.getEndTime())
            .roomNumber(shift.getRoom().getRoomNumber())
            .assignedEmployeeId(shift.getAssignedEmployee() != null ? shift.getAssignedEmployee().getId() : null)
            .assignedEmployeeName(shift.getAssignedEmployee() != null
                    ? shift.getAssignedEmployee().getName() + " " + shift.getAssignedEmployee().getSurname()
                    : null)
            .build();

        return dto;
    }

    public void saveShift(Shift shift) {
        shiftRepository.save(shift);
    }
}
