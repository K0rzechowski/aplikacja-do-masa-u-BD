package emino.backend.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @JsonBackReference
    private Company company;

    @Column(nullable = false, length = 9)
    private String phoneNumber;

    @OneToMany(mappedBy = "employee")
    private List<Reservation> emplReservations;

    @ManyToMany
    @JoinTable(
        name = "handled_massage", 
        joinColumns = @JoinColumn(name = "employee_id"), 
        inverseJoinColumns = @JoinColumn(name = "massage_id"))
    private Set<Massage> massagesToHandle;

    @Builder(builderMethodName = "emplBuilder") 
    public Employee(Long id, String password, String email, String name, String surname, LocalDate dateOfBirth, UserRole role, Company company, String phoneNumber) {
        super(id, password, email, name, surname, dateOfBirth, role, null);
        this.phoneNumber = phoneNumber;
        this.company = company;
    }
}