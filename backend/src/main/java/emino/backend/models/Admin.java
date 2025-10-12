package emino.backend.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Admin extends User {
    
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    @JsonBackReference
    private Company company;

    @Builder(builderMethodName = "adminBuilder") 
    public Admin(Long id, String password, String email, String name, String surname, LocalDate dateOfBirth, UserRole role, Company company) {
        super(id, password, email, name, surname, dateOfBirth, role, null);
        this.company = company;
    }
}