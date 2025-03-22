package hcmut.examify.Services;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.CallableStatement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import hcmut.examify.DTOs.AnswerDTO;
import hcmut.examify.DTOs.QuestionDTO;
import hcmut.examify.DTOs.ResponseObject;
import hcmut.examify.DTOs.TestsDTO;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class TestsService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;


    public TestsService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<ResponseObject> FNC_getAllTests(Long teacherId) {
        try {
            String tests = jdbcTemplate.queryForObject(
                    "SELECT get_all_test_of_teacher(teacherId)",
                    String.class
            );
            if (tests == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully with data = null", tests));
            }

            JsonNode jsonNode = objectMapper.readTree(tests);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully", jsonNode));
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
                    .body(new ResponseObject("ERROR", "Error getting FNC_getAllTests(): " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObject> FNC_getTestById(Long teacherId, Long testId) {
        try {
            String test = jdbcTemplate.queryForObject(
                    "SELECT get_test_of_teacher_by_testID(teacherId, testId)",
                    String.class
            );
            if (test == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getTestById() successfully with data = null", test));
            }

            JsonNode jsonNode = objectMapper.readTree(test);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getTestById() successfully", jsonNode));
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
                    .body(new ResponseObject("ERROR", "Error getting FNC_getTestById(): " + e.getMessage(), null));
        }
    }


    public ResponseEntity<ResponseObject> PROC_addTest(TestsDTO testsDTO) {
        try {
            System.out.println("testsDTO: " + testsDTO);

            // Định dạng cho ISO 8601 và định dạng SQL
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Chuyển đổi timeOpen
            LocalDateTime timeOpen = LocalDateTime.parse(testsDTO.getTimeOpen(), isoFormatter);
            String formattedTimeOpen = timeOpen.format(sqlFormatter);

            // Chuyển đổi timeClose
            LocalDateTime timeClose = LocalDateTime.parse(testsDTO.getTimeClose(), isoFormatter);
            String formattedTimeClose = timeClose.format(sqlFormatter);

            jdbcTemplate.execute(
                "CALL create_test(?, ?, ?, ?, ?, ?, ?, ?)",
                (PreparedStatementCallback<Void>) ps -> {
                    ps.setString(1, testsDTO.getTitle());
                    ps.setString(2, testsDTO.getDescription());
                    ps.setString(3, testsDTO.getPasscode());
                    ps.setInt(4, testsDTO.getTestTime());
                    ps.setTimestamp(5, Timestamp.valueOf(formattedTimeOpen)); // Truyền Timestamp
                    ps.setTimestamp(6, Timestamp.valueOf(formattedTimeClose)); // Truyền Timestamp
                    ps.setInt(7, testsDTO.getTeacherId());
                    ps.setInt(8, testsDTO.getNumberOfQuestion());

                    ps.execute();
                    return null;
                }
            );
            return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("OK", "Query to update PROC_addTest() successfully", null));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("ERROR", "Error updating PROC_addTest(): " + e.getMessage(), null));
        }
    }

}
