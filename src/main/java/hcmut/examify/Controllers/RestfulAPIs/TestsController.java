package hcmut.examify.Controllers.RestfulAPIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hcmut.examify.DTOs.ResponseObject;
import hcmut.examify.DTOs.TestsDTO;
import hcmut.examify.Services.TestsService;

@RestController
@RequestMapping("/tests")
public class TestsController {
    @Autowired
    private TestsService testsService;

    @PostMapping
    public ResponseEntity<ResponseObject> addStudent(@RequestBody TestsDTO testsDTO){
        
        return testsService.PROC_addTest(testsDTO);
    }

}
