package emino.backend.models;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.Check;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Check(constraints = "start_time < end_time")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "shift_id", "start_time" }))
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @JsonIgnore
    @OneToMany(mappedBy = "timeSlot")
    private Set<Reservation> reservations;
}
