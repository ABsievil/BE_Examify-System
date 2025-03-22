package hcmut.examify.Services;

import java.sql.Timestamp;
import java.sql.Types;
import java.sql.CallableStatement;
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
            
            // Sau đó, thêm các câu hỏi và câu trả lời
            for (QuestionDTO question : testsDTO.getQestions()) {
                Long questionId = jdbcTemplate.execute(
                    "CALL create_question(?, ?, ?)",
                    (CallableStatementCallback<Long>) cs -> {
                        cs.setString(1, question.getContent());
                        cs.setDouble(2, question.getScore());
                        
                        // Đăng ký tham số OUT để nhận question_id được tạo
                        cs.registerOutParameter(3, Types.BIGINT);
                        
                        cs.execute();
                        
                        // Lấy question_id được trả về từ stored procedure
                        return cs.getLong(3);
                    }
                );
                
                // Thêm các câu trả lời cho mỗi câu hỏi
                for (AnswerDTO answer : question.getAnswers()) {
                    jdbcTemplate.execute(
                        "CALL create_answer(?, ?, ?)",
                        (PreparedStatementCallback<Void>) ps -> {
                            ps.setString(1, answer.getContent());
                            ps.setBoolean(2, answer.getIsCorrect());
                            
                            ps.execute();
                            return null;
                        }
                    );
                }
            }
            
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
