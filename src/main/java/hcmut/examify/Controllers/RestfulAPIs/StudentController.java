package hcmut.examify.Controllers.RestfulAPIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hcmut.examify.DTOs.ResponseObject;
import hcmut.examify.DTOs.TestsDTO;
import hcmut.examify.Services.StudentService;
import hcmut.examify.Services.TestsService;

@RestController
@RequestMapping("/students")
public class StudentController {
     @Autowired
    private StudentService studentService;

    @GetMapping("/tests")
    public ResponseEntity<ResponseObject> getTestByPasscode(@RequestParam("passcode") String passcode) {
        return studentService.FNC_getTestInforByPasscode(passcode);
    }

    @GetMapping("/{studentId}/answers")
    public ResponseEntity<ResponseObject> getStudentAnswer(
        @PathVariable("studentId") Integer studentId,
        @RequestParam("questionId") Integer questionId ) {
        return studentService.FNC_getStudentAnswer(studentId, questionId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createStudentAnswer(
        @PathVariable("studentId") Integer studentId,
        @RequestParam("questionId") Integer questionId ){
        return studentService.PROC_createStudentAnswer(studentId, questionId);
    }

    @PutMapping
    public ResponseEntity<ResponseObject> updateStudentAnswer(
        @PathVariable("studentId") Integer studentId,
        @RequestParam("questionId") Integer questionId,
        @RequestParam("isCorrect") Boolean isCorrect ){
        return studentService.PROC_updateStudentAnswer(studentId, questionId, isCorrect);
    }

}
