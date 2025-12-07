package emino.backend.dto;

import emino.backend.models.MassageForCompany;
import emino.backend.models.Shift;
import emino.backend.models.Status;
import emino.backend.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    
    private Long id; 
    private User customer;
    private Status status;
    private Shift shift;
    private MassageForCompany massageForCompany;
}
