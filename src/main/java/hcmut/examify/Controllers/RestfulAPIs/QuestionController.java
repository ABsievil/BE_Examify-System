package hcmut.examify.Controllers.RestfulAPIs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hcmut.examify.DTOs.ResponseObject;
import hcmut.examify.Services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping
    public ResponseEntity<ResponseObject> FNC_getAllQuestions() {
        return questionService.FNC_getAllQuestions();
    }

    @GetMapping
    public ResponseEntity<ResponseObject> FNC_getQuestionById(@RequestParam("testId") Long questionId) {
        return questionService.FNC_getQuestionById(questionId);
    }

}