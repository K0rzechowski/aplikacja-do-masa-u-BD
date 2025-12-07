package emino.backend.models;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Builder
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne()
    @JoinColumn(name = "massage_id")
    private MassageForCompany massage;

    @ManyToOne()
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    @JsonIgnore
    @OneToMany(mappedBy = "shift")
    private Set<Reservation> reservation;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}