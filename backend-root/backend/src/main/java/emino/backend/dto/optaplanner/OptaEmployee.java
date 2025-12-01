package emino.backend.dto.optaplanner;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    
    @JsonProperty("skillSet")
    private Set<String> skills;
}