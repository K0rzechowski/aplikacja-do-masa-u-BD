package emino.backend.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
@Embeddable
public class MassageSymptom implements Serializable {

    @Column(name = "massage_id")
    private Long massageId;

    @Column(name = "symptom_id")
    private Long symptomId;
}