package hcmut.examify.Services;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.CallableStatement;
import java.util.logging.Logger;
import java.util.logging.Level;

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
    private static final Logger logger = Logger.getLogger(TestsService.class.getName());


    public TestsService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        
        // Bật log SQL để xem các câu lệnh SQL được thực thi
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.springframework.jdbc.core", "DEBUG");
    }

    public ResponseEntity<ResponseObject> FNC_getAllTests(Integer teacherId) {
        try {
            logger.info("Executing FNC_getAllTests with teacherId: " + teacherId);
            
            String tests = jdbcTemplate.queryForObject(
                    "SELECT get_all_test_of_teacher(?)",
                    String.class, teacherId
            );

            if (tests == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully with data = null", null));
            }

            JsonNode jsonNode = objectMapper.readTree(tests);
            logger.info("FNC_getAllTests result: " + tests);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully", jsonNode));

        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Database error in FNC_getAllTests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "JSON processing error in FNC_getAllTests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "JSON processing error: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in FNC_getAllTests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Error getting FNC_getAllTests(): " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ResponseObject> FNC_getTestById(Long teacherId, Long testId) {
        try {
            logger.info("Executing FNC_getTestById with teacherId: " + teacherId + " (type: " + (teacherId != null ? teacherId.getClass().getName() : "null") + 
                       "), testId: " + testId + " (type: " + (testId != null ? testId.getClass().getName() : "null") + ")");
            
            // Kiểm tra function trong database
            try {
                String functionExists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) > 0 FROM pg_proc p JOIN pg_namespace n ON p.pronamespace = n.oid " +
                    "WHERE p.proname = ? AND n.nspname NOT IN ('pg_catalog', 'information_schema')",
                    String.class, "get_test_of_teacher_by_testid"
                );
                logger.info("Function exists in database: " + functionExists);
                
                // Kiểm tra function signature
                String functionSignature = jdbcTemplate.queryForObject(
                    "SELECT p.proname || '(' || pg_get_function_arguments(p.oid) || ')' " +
                    "FROM pg_proc p JOIN pg_namespace n ON p.pronamespace = n.oid " +
                    "WHERE p.proname = ? AND n.nspname NOT IN ('pg_catalog', 'information_schema')",
                    String.class, "get_test_of_teacher_by_testid"
                );
                logger.info("Function signature in database: " + functionSignature);
            } catch (Exception e) {
                logger.warning("Could not check function in database: " + e.getMessage());
            }
            
            // Thử ép kiểu tham số từ Long sang Integer
            Integer teacherIdInt = teacherId != null ? teacherId.intValue() : null;
            Integer testIdInt = testId != null ? testId.intValue() : null;
            
            logger.info("Trying with converted parameters - teacherId: " + teacherIdInt + " (type: Integer), testId: " + testIdInt + " (type: Integer)");
            
            String test = jdbcTemplate.queryForObject(
                    "SELECT get_test_of_teacher_by_testID(?, ?)",
                    String.class, teacherIdInt, testIdInt
            );
            if (test == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getTestById() successfully with data = null", test));
            }

            JsonNode jsonNode = objectMapper.readTree(test);
            logger.info("FNC_getTestById result: " + test);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getTestById() successfully", jsonNode));
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Database error in FNC_getTestById", e);
            
            // Thêm kiểm tra chi tiết về stored function
            try {
                logger.info("Attempting to check if function exists with different parameter types...");
                String query = "SELECT proname, pg_get_function_arguments(oid) as args " +
                               "FROM pg_proc WHERE proname LIKE 'get_test_of_teacher_by_testid%'";
                jdbcTemplate.query(query, (rs, rowNum) -> {
                    logger.info("Found function: " + rs.getString("proname") + 
                               " with arguments: " + rs.getString("args"));
                    return null;
                });
            } catch (Exception ex) {
                logger.warning("Failed to check function definitions: " + ex.getMessage());
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "JSON processing error in FNC_getTestById", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "JSON processing error: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in FNC_getTestById", e);
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

            logger.info("PROC_createTest parameters: " + 
                        "\nTitle: " + testsDTO.getTitle() +
                        "\nDescription: " + testsDTO.getDescription() +
                        "\nTestTime: " + testsDTO.getTestTime() +
                        "\nTimeOpen: " + formattedTimeOpen +
                        "\nTimeClose: " + formattedTimeClose +
                        "\nTeacherId: " + testsDTO.getTeacherId() +
                        "\nNumberOfQuestion: " + testsDTO.getNumberOfQuestion());

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
                
                logger.info("Executing stored procedure: create_test with parameters set");
                cs.execute();
                
                // Lấy giá trị đầu ra từ tham số INOUT
                Integer result = cs.getInt(8);
                logger.info("create_test returned testId: " + result);
                return result;
            }
        );
            System.out.println("newTestId: " + newTestId);
            logger.info("New test created with ID: " + newTestId);

            // code chạy đéo dc, proc trên đổi thành func để lấy testId cho 2 method dưới đây
            // Sau đó, thêm các câu hỏi và câu trả lời
            for (QuestionDTO question : testsDTO.getQuestions()) {
                System.out.println("question: " + question);
                logger.info("Processing question: " + question.getContent());
                
                // Lưu ý: Không sử dụng cú pháp {CALL ...} ở đây vì gây lỗi với database/driver hiện tại
                Integer questionId = jdbcTemplate.execute(
                    "CALL create_question(?, ?, ?, ?)",
                    (CallableStatementCallback<Integer>) cs -> {
                        cs.setString(1, question.getContent());
                        cs.setFloat(2, question.getScore());
                        cs.setInt(3, newTestId);
                        
                        // Đăng ký tham số OUT
                        cs.registerOutParameter(4, Types.INTEGER);
                        
                        logger.info("Executing stored procedure: create_question with content: " + question.getContent() + 
                                   ", score: " + question.getScore() + ", testId: " + newTestId);
                        cs.execute();
                        
                        // Lấy giá trị đầu ra
                        Integer result = cs.getInt(4);
                        logger.info("create_question returned questionId: " + result);
                        return result;
                    }
                );
                
                System.out.println("questionId: " + questionId);
                logger.info("Question created with ID: " + questionId);
    
                // Thêm các câu trả lời cho mỗi câu hỏi
                for (AnswerDTO answer : question.getAnswers()) {
                    jdbcTemplate.execute(
                        "CALL create_answer(?, ?, ?)",
                        (PreparedStatementCallback<Void>) ps -> {
                            ps.setString(1, answer.getContent());
                            ps.setBoolean(2, answer.getIsCorrect());
                            ps.setInt(3, questionId);  // Chuyển từ setLong sang setInt để phù hợp với tham số function
                            
                            logger.info("Executing stored procedure: create_answer with content: " + answer.getContent() + 
                                       ", isCorrect: " + answer.getIsCorrect() + ", questionId: " + questionId);
                            ps.execute();
                            return null;
                        }
                    );
                    logger.info("Answer created: " + answer.getContent() + ", isCorrect: " + answer.getIsCorrect());
                }
                System.out.println("add answer success");
                logger.info("All answers added successfully for question ID: " + questionId);
            }


            return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("OK", "Query to update PROC_addTest() successfully", null));
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Database error in PROC_createTest", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in PROC_createTest", e);
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
            
            logger.info("FNC_addTest parameters: " + 
                       "\nTitle: " + testsDTO.getTitle() +
                       "\nDescription: " + testsDTO.getDescription() +
                       "\nTestTime: " + testsDTO.getTestTime() +
                       "\nTimeOpen: " + formattedTimeOpen +
                       "\nTimeClose: " + formattedTimeClose +
                       "\nTeacherId: " + testsDTO.getTeacherId() +
                       "\nNumberOfQuestion: " + testsDTO.getNumberOfQuestion());
            
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
                logger.warning("add_test function returned null");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully with data = null", null));
            }

            System.out.println("newTestId: " + newTestId);
            logger.info("New test created with ID: " + newTestId);

            // Sau đó, thêm các câu hỏi và câu trả lời
            for (QuestionDTO question : testsDTO.getQuestions()) {
                System.out.println("question: " + question);
                logger.info("Processing question: " + question.getContent());

                Integer questionId = jdbcTemplate.queryForObject(
                    "SELECT add_question(?, ?, ?)",
                    Integer.class,
                    question.getContent(),
                    question.getScore(),
                    newTestId
                );
                
                System.out.println("questionId: " + questionId);
                logger.info("Question created with ID: " + questionId);

                // Thêm các câu trả lời cho mỗi câu hỏi
                for (AnswerDTO answer : question.getAnswers()) {
                    jdbcTemplate.execute(
                        "SELECT add_answer(?, ?, ?)",
                        (PreparedStatementCallback<Void>) ps -> {
                            ps.setString(1, answer.getContent());
                            ps.setBoolean(2, answer.getIsCorrect());
                            ps.setInt(3, questionId);  // Chuyển từ setLong sang setInt để phù hợp với tham số function
                            
                            logger.info("Executing function: add_answer with content: " + answer.getContent() + 
                                       ", isCorrect: " + answer.getIsCorrect() + ", questionId: " + questionId);
                            ps.execute();
                            return null;
                        }
                    );
                    logger.info("Answer created: " + answer.getContent() + ", isCorrect: " + answer.getIsCorrect());
                }
                System.out.println("add answer success");
                logger.info("All answers added successfully for question ID: " + questionId);
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Query to get FNC_getAllTests() successfully", null));

        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Database error in FNC_addTest", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Database error: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in FNC_addTest", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "Error getting FNC_getAllTests(): " + e.getMessage(), null));
        }
    }
}
