package emino.backend.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class Employee extends User {
    
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 9)
    private String phoneNumber;

    @OneToOne(mappedBy = "employee")
    private Reservation reservation;

    @Builder(builderMethodName = "emplBuilder") 
    public Employee(Long id, String password, String email, String name, String surname, LocalDate dateOfBirth, String phoneNumber, UserRole role, Company company) {
        super(id, password, email, name, surname, dateOfBirth,  role, null);
        this.phoneNumber = phoneNumber;
        this.company = company;
    }
}