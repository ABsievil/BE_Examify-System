package hcmut.examify.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
    private Integer studentId;
    private Integer testId;
    private String startTime;   // create result
    private String endTime; // update result
}
