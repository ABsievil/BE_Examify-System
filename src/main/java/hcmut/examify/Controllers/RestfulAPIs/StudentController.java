package hcmut.examify.Controllers.RestfulAPIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hcmut.examify.DTOs.ResponseObject;
import hcmut.examify.Services.TestsService;

@RestController
@RequestMapping("/students")
public class StudentController {
     @Autowired
    private TestsService testsService;

    @GetMapping("/tests")
    public ResponseEntity<ResponseObject> getTestByPasscode(@RequestParam("passcode") String passcode) {
        return testsService.FNC_getTestInforByPasscode(passcode);
    }
}
