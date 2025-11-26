package emino.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import emino.backend.dto.optaplanner.OptaSchedule;
import emino.backend.dto.optaplanner.OptaShift;
import emino.backend.mapper.OptaPlannerMapper;
import emino.backend.models.Company;
import emino.backend.models.Employee;
import emino.backend.models.EmployeeAvailability;
import emino.backend.models.Shift;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeAvailabilityRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.ShiftRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class ScheduleOptimizationService {
    
    private EmployeeRepository employeeRepository;
    private CompanyRepository companyRepository;
    private ShiftRepository shiftRepository;
    private EmployeeAvailabilityRepository availabilityRepository;
    private RestTemplate restTemplate;
    private OptaPlannerMapper optaPlannerMapper;

    @Value("${optaplanner.service.url:http://localhost:8081}")
    private String optaPlannerUrl;

    public OptaSchedule optimizeSchedule(Long companyId, LocalDateTime startTime, LocalDateTime endTime) {
        Company company =  companyRepository.findById(companyId)
        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));

        List<Employee> employees = employeeRepository.findByCompany(company);
        List<Shift> shifts = shiftRepository.findByCompanyIdAndStartTimeBetween(companyId, startTime, endTime);
        List<EmployeeAvailability> availabilities = availabilityRepository.findByDateBetween(startTime.toLocalDate(), endTime.toLocalDate());

        OptaSchedule problem = optaPlannerMapper.toOptaPlannerSchedule(companyId, employees, shifts, availabilities, startTime.toLocalDate(), endTime.toLocalDate());

        String solveUrl = optaPlannerUrl + "/schedules/solve";
        OptaSchedule solution = restTemplate.postForObject(solveUrl, problem, OptaSchedule.class);

        if (solution != null) {
            applySolution(solution, shifts, employees);
        }

        return solution;
    }

    private void applySolution(OptaSchedule solution, List<Shift> originalShifts, List<Employee> employees) {
        for (OptaShift optaShift : solution.getShifts()) {
            Shift shift = originalShifts.stream()
                .filter(s -> s.getId().equals(optaShift.getId()))
                .findFirst()
                .orElse(null);

            if (shift != null && optaShift.getEmployee() != null) {
                String employeeName = optaShift.getEmployee().getName();
                Employee employee = employees.stream()
                    .filter(e -> (e.getName() + " " + e.getSurname()).equals(employeeName))
                    .findFirst()
                    .orElse(null);

                if (employee != null) {
                    shift.setAssignedEmployee(employee);
                    shiftRepository.save(shift);
                }
            }
        }
    }
}