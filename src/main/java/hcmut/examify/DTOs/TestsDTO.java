package hcmut.examify.DTOs;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestsDTO {
    private String title;
    private String description;
    private String passcode;
    private Integer testTime;
    private String timeOpen;
    private String timeClose;
    private Integer teacherId;
    private Integer numberOfQuestion;
}