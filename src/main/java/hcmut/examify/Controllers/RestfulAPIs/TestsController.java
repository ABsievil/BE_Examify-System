package hcmut.examify.Controllers.RestfulAPIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hcmut.examify.DTOs.QuestionDTO;
import hcmut.examify.DTOs.ResponseObject;
import hcmut.examify.DTOs.TestsDTO;
import hcmut.examify.Services.TestsService;

@RestController
@RequestMapping("/tests")
public class TestsController {
    @Autowired
    private TestsService testsService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllTests(@RequestParam("teacherId") Integer teacherId) {
        return testsService.FNC_getAllTests(teacherId);
    }

    @GetMapping("/{testId}")
    public ResponseEntity<ResponseObject> getTestById(
        @PathVariable Long testId,
        @RequestParam("teacherId") Long teacherId) {
        return testsService.FNC_getTestById(teacherId, testId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addTest(@RequestBody TestsDTO testsDTO){
        return testsService.PROC_addTest(testsDTO);
    }
}
