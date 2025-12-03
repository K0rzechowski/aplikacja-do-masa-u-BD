package emino.backend.dto.optaplanner;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import emino.backend.models.AvailabilityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptaAvailability {

    private Long id;
    private OptaEmployee employee;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate date;
    private AvailabilityType availabilityType;
}