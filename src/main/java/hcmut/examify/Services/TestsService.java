package hcmut.examify.Services;

import java.sql.Timestamp;
import java.sql.Types;
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

@Service
public class TestsService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;


    public TestsService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<ResponseObject> FNC_getAllTests() {
        try {
            String tests = jdbcTemplate.queryForObject(
                    "SELECT get_all_test_of_teacher()",
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


    public ResponseEntity<ResponseObject> PROC_addTest(TestsDTO testsDTO) {
        try {
            // Đầu tiên, thêm thông tin cơ bản của bài kiểm tra và lấy ID được tạo
            Long testId = jdbcTemplate.execute(
                "CALL create_test(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                (PreparedStatementCallback<Long>) ps -> {
                    ps.setString(1, testsDTO.getTitle());
                    ps.setString(2, testsDTO.getDescription());
                    ps.setString(3, testsDTO.getPasscode());                    
                    ps.setInt(4, testsDTO.getTestTime());
                    ps.setTimestamp(5, Timestamp.valueOf(testsDTO.getTimeOpen().toLocalDateTime()));
                    ps.setTimestamp(6, Timestamp.valueOf(testsDTO.getTimeClose().toLocalDateTime()));
                    ps.setLong(7, testsDTO.getTeacherId());
                    ps.setInt(8, testsDTO.getNumberOfQuestion());

                    ps.execute();
                    return null;
                }
            );
            
            return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("OK", "Thêm bài kiểm tra thành công", testId));
                
        } catch (DataAccessException e) {
            // Xử lý lỗi liên quan đến truy cập dữ liệu
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("ERROR", "Lỗi cơ sở dữ liệu: " + e.getMessage(), null));
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("ERROR", "Lỗi khi thêm bài kiểm tra: " + e.getMessage(), null));
        }
    }

}
