package emino.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.dto.ShiftResponseDto;
import emino.backend.models.Company;
import emino.backend.models.CompanyOpenTime;
import emino.backend.models.MassageForCompany;
import emino.backend.models.Room;
import emino.backend.models.Shift;
import emino.backend.repositories.ShiftRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftService {
    
    private final ShiftRepository shiftRepository;
    private final CompanyService companyService;

    public Optional<Shift> getShiftById(Long shiftId) {
        return shiftRepository.findById(shiftId);
    }

    public int generateShiftsForNextWeek(Company company, LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(7);
        Optional<CompanyOpenTime> openTime = companyService.getCompanyOpenTime(company.getId()); 
        int dbOpenTime = openTime.get().getOpenTime().getHour();
        int dbCloseTime = openTime.get().getCloseTime().getHour();
        
        List<Shift> shifts = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            for (int hour = dbOpenTime; hour < dbCloseTime; hour++) {
                LocalDateTime start = date.atTime(hour, 0);
                LocalDateTime end = start.plusMinutes(55);

                for (Room room : company.getRooms()) {
                    for (MassageForCompany massage : company.getMassages()) {
                        Shift shift = Shift.builder()
                            .startTime(start)
                            .endTime(end)
                            .room(room)
                            .massage(massage)
                            .company(company)
                            .assignedEmployee(null)
                            .build();

                        shifts.add(shift);
                        // shiftRepository.save(shift);
                    }
                }
            }
        }
        shiftRepository.saveAll(shifts);
        return shifts.size();
    }

    public List<Shift> getShiftsByCompany(Long companyId, LocalDateTime startTime, LocalDateTime endTime) {
        return shiftRepository.findByCompanyIdAndStartTimeBetween(companyId, startTime, endTime);
    }

    public ShiftResponseDto getShiftResponse(Shift shift) {
        ShiftResponseDto dto = ShiftResponseDto.builder()
            .companyId(shift.getCompany().getId())
            .startTime(shift.getStartTime())
            .endTime(shift.getEndTime())
            .roomNumber(shift.getRoom().getRoomNumber())
            .massageName(shift.getMassage().getMassage().getName())
            .assignedEmployeeName(shift.getAssignedEmployee() != null 
                ? shift.getAssignedEmployee().getName() + " " + shift.getAssignedEmployee().getSurname() 
                : null)
            .build();

        return dto;
    }

    public void saveShift(Shift shift) {
        shiftRepository.save(shift);
    }

    // private boolean canPerformMassage(Employee employee, MassageForCompany massage) {
    //     return employee.getMassagesToHandle().contains(massage);
    // }

    // private boolean isEmployeeAvailable(Employee employee, LocalDateTime start) {
    //     if (employee.getAvailabilities() == null) {
    //         return false;
    //     }

    //     LocalDate date = start.toLocalDate();

    //     return employee.getAvailabilities().stream()
    //         .filter(availability -> availability.getDate().equals(date))
    //         .anyMatch(availability ->
    //             (availability.getAvailabilityType() == emino.backend.models.AvailabilityType.AVAILABLE ||
    //             availability.getAvailabilityType() == emino.backend.models.AvailabilityType.DESIRED)
    //     );
    // }

    // private boolean hasOverlappingShift(Employee employee, LocalDateTime start, LocalDateTime end) {
    //     if (employee.getShifts() == null) {
    //         return false;
    //     }

    //     return employee.getShifts().stream().anyMatch(shift ->
    //         start.isBefore(shift.getEndTime()) && end.isAfter(shift.getStartTime())
    //     );
    // }

    // private int availabilityScore(Employee employee, LocalDateTime start) {
    //     if (employee.getAvailabilities() == null) {
    //         return -1;
    //     }

    //     LocalDate date = start.toLocalDate();

    //     return employee.getAvailabilities().stream()
    //         .filter(availability -> availability.getDate().equals(date))
    //         .map(availability -> {
    //             return switch (availability.getAvailabilityType()) {
    //                 case DESIRED -> 2;
    //                 case AVAILABLE -> 1;
    //                 case UNDESIRED -> 0;
    //                 case UNAVAILABLE -> -1;
    //                 default -> -1;
    //             };
    //         })
    //         .findFirst()
    //         .orElse(-1);
    // }

    // private Employee findEmployeeForShift(Company company, MassageForCompany massage, LocalDateTime start, LocalDateTime end) {
    //     return company.getEmployees().stream()
    //         .filter(employee -> canPerformMassage(employee, massage))
    //         .filter(employee -> !hasOverlappingShift(employee, start, end))
    //         .filter(employee -> availabilityScore(employee, start) > -1)
    //         .max(Comparator.comparingInt(employee -> availabilityScore(employee, start)))
    //         .orElse(null);
    // }
}
