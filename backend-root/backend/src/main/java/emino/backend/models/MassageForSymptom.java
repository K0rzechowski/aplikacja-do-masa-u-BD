package emino.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MassageForSymptom {

    @EmbeddedId
    private MassageSymptom id;

    @ManyToOne
    @MapsId("massageId")
    @JoinColumn(name = "massage_id")
    private Massage massage;

    @ManyToOne
    @MapsId("symptomId")
    @JoinColumn(name = "symptom_id")
    private Symptom symptom;

    @Column(nullable=false)
    private Double effectivenessScore;
}