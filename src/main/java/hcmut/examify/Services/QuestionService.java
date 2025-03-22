package hcmut.examify.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hcmut.examify.DTOs.ResponseObject;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public QuestionService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public ResponseEntity<ResponseObject> FNC_getAllQuestions() {
        try {
            String questions = jdbcTemplate.queryForObject(
                    "SELECT get_all_question_of_test()",
                    String.class
            );
            if (questions == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getAllQuestions() successfully with data = null", questions));
            }

            JsonNode jsonNode = objectMapper.readTree(questions);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getAllQuestions() successfully", jsonNode));
        } catch (DataAccessException e) {
            // Xử lý lỗi liên quan đến truy cập dữ liệu
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (JsonProcessingException e) {
            // Xử lý lỗi khi parse JSON
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "JSON processing error: " + e.getMessage(), null));
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Error getting FNC_getAllQuestions(): " + e.getMessage(), null));
        }
    }
}
