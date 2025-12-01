package emino.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

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

@Service
@RequiredArgsConstructor
public class ScheduleOptimizationService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeAvailabilityRepository availabilityRepository;
    private final RestTemplate restTemplate;
    private final OptaPlannerMapper optaPlannerMapper;

    @Value("${optaplanner.service.url:http://optaplanner:8081}")
    private String optaPlannerUrl;

    public OptaSchedule optimizeSchedule(OptaSchedule problem) throws JsonProcessingException{
        String optimizeUrl = optaPlannerUrl + "/schedule/solve";

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        // String json = mapper.writeValueAsString(problem);
        ResponseEntity<OptaSchedule> response = restTemplate.postForEntity(optimizeUrl, problem,
                OptaSchedule.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to optimize schedule");
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