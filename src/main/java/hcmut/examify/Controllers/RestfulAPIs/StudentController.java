package hcmut.examify.Controllers.RestfulAPIs;

import hcmut.examify.DTOs.AnswerDTO;
import hcmut.examify.DTOs.ResultDTO;
import hcmut.examify.Services.ResultService;
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
import hcmut.examify.Services.StudentService;

@RestController
@RequestMapping("/students")
public class StudentController {
     @Autowired
    private StudentService studentService;

     @Autowired
     private ResultService resultService;

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

    @PostMapping("/{studentId}/answers")
    public ResponseEntity<ResponseObject> createStudentAnswer(
        @PathVariable("studentId") Integer studentId,
        @RequestParam("questionId") Integer questionId ){
        return studentService.PROC_createStudentAnswer(studentId, questionId);
    }

    @PutMapping("/{studentId}/answers/{answerId}")
    public ResponseEntity<ResponseObject> updateStudentAnswer(
        @PathVariable("studentId") Integer studentId,
        @PathVariable("answerId") Integer answerId,
        @RequestBody AnswerDTO answerDTO ) {
        return studentService.PROC_updateStudentAnswer(answerDTO, studentId, answerId);
    }

    // NOTE: do not require endtime IN BODY -> studentid, testid, starttime
    @PostMapping("/{studentId}/results")
    public ResponseEntity<ResponseObject> createStudentResult(@RequestBody ResultDTO resultDTO) {
        return resultService.PROC_createResult(resultDTO);
    }

    // NOTE: do not require starttime IN BODY -> studentid, testid, endtime
    @PutMapping("{studentId}/results")
    public ResponseEntity<ResponseObject> updateStudentResult(@RequestBody ResultDTO resultDTO) {
        return resultService.PROC_updateResult(resultDTO);
    }

    @GetMapping("/{studentId}/results")
    public ResponseEntity<ResponseObject> getAllTResultsOfStudent(@PathVariable Integer studentId) {
        return resultService.FNC_getAllResultsByStudentId(studentId);
    }

}
