package emino.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import emino.backend.dto.optaplanner.OptaSchedule;
import emino.backend.dto.optaplanner.OptaShift;
import emino.backend.mapper.OptaPlannerMapper;
import emino.backend.models.Employee;
import emino.backend.models.Shift;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.EmployeeAvailabilityRepository;
import emino.backend.repositories.EmployeeRepository;
import emino.backend.repositories.ShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleOptimizationService {
    
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeAvailabilityRepository availabilityRepository;
    private final RestTemplate restTemplate;
    private final OptaPlannerMapper optaPlannerMapper;

    @Value("${optaplanner.service.url:http://optaplanner:8081}")
    private String optaPlannerUrl;

    public OptaSchedule optimizeSchedule(OptaSchedule problem) {
        String optimizeUrl = optaPlannerUrl + "/schedule/solve";

        try {
            log.info("Sending schedule to OptaPlanner service with {} shifts and {} employees", 
                problem.getShifts().size(), problem.getEmployees().size());

            ResponseEntity<OptaSchedule> response = restTemplate.postForEntity(optimizeUrl, problem, OptaSchedule.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Received optimized schedule from OptaPlanner service");
                return response.getBody();
            } else {
                log.error("Failed to optimize schedule: received status {}", response.getStatusCode());
                throw new RuntimeException("Failed to optimize schedule");
            }
        } catch (Exception e) {
            log.error("Error while communicating with OptaPlanner service", e);
            throw new RuntimeException("Error while optimizing schedule", e);
        }
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