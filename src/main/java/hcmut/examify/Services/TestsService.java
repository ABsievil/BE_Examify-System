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

    public ResponseEntity<ResponseObject> FNC_getAllTests(Integer teacherId) {
        try {
            String tests = jdbcTemplate.queryForObject(
                    "SELECT get_all_test_of_teacher(?)",
                    String.class, teacherId
            );

            if (tests == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully with data = null", null));
            }

            JsonNode jsonNode = objectMapper.readTree(tests);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully", jsonNode));

        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "JSON processing error: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Error getting FNC_getAllTests(): " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObject> FNC_getTestById(Long teacherId, Long testId) {
        try {
            String test = jdbcTemplate.queryForObject(
                    "SELECT get_test_of_teacher_by_testID(?, ?)",
                    String.class, teacherId, testId
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


    public ResponseEntity<ResponseObject> PROC_createTest(TestsDTO testsDTO) {
        try {
            // Định dạng cho ISO 8601 và định dạng SQL
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Chuyển đổi timeOpen
            LocalDateTime timeOpen = LocalDateTime.parse(testsDTO.getTimeOpen(), isoFormatter);
            String formattedTimeOpen = timeOpen.format(sqlFormatter);

            // Chuyển đổi timeClose
            LocalDateTime timeClose = LocalDateTime.parse(testsDTO.getTimeClose(), isoFormatter);
            String formattedTimeClose = timeClose.format(sqlFormatter);

            Integer newTestId = jdbcTemplate.execute(
            "{CALL create_test(?, ?, ?, ?, ?, ?, ?, ?)}",
            (CallableStatementCallback<Integer>) cs -> {
                cs.setString(1, testsDTO.getTitle());
                cs.setString(2, testsDTO.getDescription());
                cs.setInt(3, testsDTO.getTestTime());
                cs.setTimestamp(4, Timestamp.valueOf(formattedTimeOpen));
                cs.setTimestamp(5, Timestamp.valueOf(formattedTimeClose));
                cs.setInt(6, testsDTO.getTeacherId());
                cs.setInt(7, testsDTO.getNumberOfQuestion());
                
                // Đăng ký tham số OUT
                cs.registerOutParameter(8, Types.INTEGER);
                
                cs.execute();
                
                // Lấy giá trị đầu ra từ tham số INOUT
                return cs.getInt(8);
            }
        );
            System.out.println("newTestId: " + newTestId);

            // code chạy đéo dc, proc trên đổi thành func để lấy testId cho 2 method dưới đây
            // Sau đó, thêm các câu hỏi và câu trả lời
            for (QuestionDTO question : testsDTO.getQuestions()) {
                System.out.println("question: " + question);
                
                // Lưu ý: Không sử dụng cú pháp {CALL ...} ở đây vì gây lỗi với database/driver hiện tại
                Integer questionId = jdbcTemplate.execute(
                    "CALL create_question(?, ?, ?, ?)",
                    (CallableStatementCallback<Integer>) cs -> {
                        cs.setString(1, question.getContent());
                        cs.setFloat(2, question.getScore());
                        cs.setInt(3, newTestId);
                        
                        // Đăng ký tham số OUT
                        cs.registerOutParameter(4, Types.INTEGER);
                        
                        cs.execute();
                        
                        // Lấy giá trị đầu ra
                        return cs.getInt(4);
                    }
                );
                
                System.out.println("questionId: " + questionId);
    
                // Thêm các câu trả lời cho mỗi câu hỏi
                for (AnswerDTO answer : question.getAnswers()) {
                    jdbcTemplate.execute(
                        "CALL create_answer(?, ?, ?)",
                        (PreparedStatementCallback<Void>) ps -> {
                            ps.setString(1, answer.getContent());
                            ps.setBoolean(2, answer.getIsCorrect());
                            ps.setInt(3, questionId);  // Chuyển từ setLong sang setInt để phù hợp với tham số function
                            
                            ps.execute();
                            return null;
                        }
                    );
                }
                System.out.println("add answer success");
            }


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

    public ResponseEntity<ResponseObject> FNC_addTest(TestsDTO testsDTO) {
        try {
            // Định dạng cho ISO 8601 và định dạng SQL
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Chuyển đổi timeOpen
            LocalDateTime timeOpen = LocalDateTime.parse(testsDTO.getTimeOpen(), isoFormatter);
            String formattedTimeOpen = timeOpen.format(sqlFormatter);

            // Chuyển đổi timeClose
            LocalDateTime timeClose = LocalDateTime.parse(testsDTO.getTimeClose(), isoFormatter);
            String formattedTimeClose = timeClose.format(sqlFormatter);
            
            // The most likely error occurs at Timestamp
            Integer newTestId = jdbcTemplate.queryForObject(
                    "SELECT add_test(?, ?, ?, ?, ?, ?, ?)",
                    Integer.class, 
                    testsDTO.getTitle(),
                    testsDTO.getDescription(),  
                    testsDTO.getTestTime(),
                    Timestamp.valueOf(formattedTimeOpen),
                    Timestamp.valueOf(formattedTimeClose),
                    testsDTO.getTeacherId(),
                    testsDTO.getNumberOfQuestion()
            );


            if (newTestId == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully with data = null", null));
            }

            System.out.println("newTestId: " + newTestId);

            // Sau đó, thêm các câu hỏi và câu trả lời
            for (QuestionDTO question : testsDTO.getQuestions()) {
                System.out.println("question: " + question);

                Integer questionId = jdbcTemplate.queryForObject(
                    "SELECT add_question(?, ?, ?)",
                    Integer.class,
                    question.getContent(),
                    question.getScore(),
                    newTestId
                );
                
                System.out.println("questionId: " + questionId);

                // Thêm các câu trả lời cho mỗi câu hỏi
                for (AnswerDTO answer : question.getAnswers()) {
                    jdbcTemplate.execute(
                        "SELECT add_answer(?, ?, ?)",
                        (PreparedStatementCallback<Void>) ps -> {
                            ps.setString(1, answer.getContent());
                            ps.setBoolean(2, answer.getIsCorrect());
                            ps.setInt(3, questionId);  // Chuyển từ setLong sang setInt để phù hợp với tham số function
                            
                            ps.execute();
                            return null;
                        }
                    );
                }
                System.out.println("add answer success");
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully", null));

        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Error getting FNC_getAllTests(): " + e.getMessage(), null));
        }
    }
}
