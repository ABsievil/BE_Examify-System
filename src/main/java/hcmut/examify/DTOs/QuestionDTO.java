package hcmut.examify.DTOs;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private String content;
    private Double score;
    private List<AnswerDTO> answers;
}
