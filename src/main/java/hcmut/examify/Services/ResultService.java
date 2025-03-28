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
public class ResultService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;


    public ResultService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<ResponseObject> FNC_getAllTestResults(Integer testId) {
        try {
            String testResults = jdbcTemplate.queryForObject(
                    "SELECT get_all_student_result(?)",
                    String.class, testId
            );

            if (testResults == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getAllTestResults() successfully with data = null", null));
            }

            JsonNode jsonNode = objectMapper.readTree(testResults);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getAllTestResults() successfully", jsonNode));

        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "JSON processing error: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Error getting FNC_getAllTestResults(): " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObject> FNC_getAllResultsByStudentId(Integer studentId) {
        try {
            String resultsOfStudent = jdbcTemplate.queryForObject(
                    "SELECT get_result_by_student_id(?)",
                    String.class, studentId
            );

            if (resultsOfStudent == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getResultByStudentIdAndTestId() successfully with data = null", null));
            }

            JsonNode jsonNode = objectMapper.readTree(resultsOfStudent);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getResultByStudentIdAndTestId() successfully", jsonNode));

        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "JSON processing error: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Error getting FNC_getResultByStudentIdAndTestId(): " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObject> FNC_getResultByStudentIdAndTestId(Integer testId, Integer studentId) {
        try {
            String studentTestResult = jdbcTemplate.queryForObject(
                    "SELECT get_result_by_student_id(?, ?)",
                    String.class, studentId, testId
            );

            if (studentTestResult == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getResultByStudentIdAndTestId() successfully with data = null", null));
            }

            JsonNode jsonNode = objectMapper.readTree(studentTestResult);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getResultByStudentIdAndTestId() successfully", jsonNode));

        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "JSON processing error: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Error getting FNC_getResultByStudentIdAndTestId(): " + e.getMessage(), null));
        }
    }
}
