package hcmut.examify.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    private Integer id;
    private Integer questionId;
    private String content;
    private Boolean isCorrect;
}