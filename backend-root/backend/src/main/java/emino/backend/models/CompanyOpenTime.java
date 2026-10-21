package emino.backend.models;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Builder
public class CompanyOpenTime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @OneToOne
    @JoinColumn(name = "branch_office_id", nullable = false, unique = true)
    @JsonIgnore
    private BranchOffice branchOffice;

    @Column(nullable=false)
    private LocalTime openTime;

    @Column(nullable=false)
    private LocalTime closeTime;    
}
