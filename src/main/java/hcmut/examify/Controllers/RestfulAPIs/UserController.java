package hcmut.examify.Controllers.RestfulAPIs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hcmut.examify.DTOs.ChangePasswordDTO;
import hcmut.examify.DTOs.NewAccountDTO;
import hcmut.examify.DTOs.ResponseObject;
import hcmut.examify.DTOs.UpdateAccountDTO;
import hcmut.examify.Services.UserService;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ResponseObject> addUser(@RequestBody NewAccountDTO newAccount) {
        return userService.PROC_addUser(newAccount);
    }

    @PutMapping
    public ResponseEntity<ResponseObject> updateUserInfor(@RequestBody UpdateAccountDTO updateAccount) {
        return userService.PROC_updateUserInfor(updateAccount);
    }

    @PutMapping("change-password")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePasswordDTO changePassword) {
        return userService.PROC_changePassword(changePassword);
    }
}
