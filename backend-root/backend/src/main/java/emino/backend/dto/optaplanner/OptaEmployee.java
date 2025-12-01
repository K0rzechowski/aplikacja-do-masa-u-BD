package emino.backend.dto.optaplanner;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptaEmployee {

    private Long id;
    private String name;
    @com.fasterxml.jackson.annotation.JsonProperty("skillSet")
    private Set<String> skills;
}
