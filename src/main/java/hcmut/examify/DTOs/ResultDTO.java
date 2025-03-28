package hcmut.examify.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
    private Integer studentId;
    private Integer testId;
    private Float totalScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
