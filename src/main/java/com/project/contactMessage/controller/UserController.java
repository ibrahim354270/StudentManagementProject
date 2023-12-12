package com.project.controller;

import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/save/{userRole}") // http://localhost:8080/user/save/Admin + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<UserResponse>> saveUser(@RequestBody @Valid UserRequest userRequest,
                                                                  @PathVariable String userRole) {
        return ResponseEntity.ok(userService.saveUser(userRequest, userRole));
    }

    @GetMapping("/getAllUserByPage/{userRole}") // http://localhost:8080/user/getAllUserByPage/Admin  + GET
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUserByPage(
            @PathVariable String userRole,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        Page<UserResponse> adminsOrDeans = userService.getUserByPage(page, size, sort, type, userRole);

        return new ResponseEntity<>(adminsOrDeans, HttpStatus.OK);
    }
    @GetMapping("/getUserById/{userId}") // http://localhost:8080/user/getUserById/1 + GET
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<BaseUserResponse> getUserById(@PathVariable Long userId){//BaseUserResponse=Student/teacher/admin
       return userService.getUserById(userId);
    }
    //HttpServletRequest ,token dan ve  ,context ile username alabiliriz. En iyisi Request (HttpServletRequest-username aldık)
    @DeleteMapping("/delete/{id}")  // http://localhost:8080/user/delete/3  + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id, HttpServletRequest httpServletRequest){

        return ResponseEntity.ok(userService.deleteUserById(id, httpServletRequest));
    }

    @PutMapping("/update/{userId}") // http://localhost:8080/user/update/3 + PUT  + JSON
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<BaseUserResponse> updateAdminDeanViceDeanForAdmin(@RequestBody @Valid UserRequest userRequest,
                                                                             @PathVariable Long userId){
        return userService.updateUser(userRequest, userId);
    }

    @PatchMapping("/updateUser") // http://localhost:8080/user/updateUser + PATCH + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public ResponseEntity<String> updateUserForUsers(@RequestBody @Valid
                                                         UserRequestWithoutPassword userRequestWithoutPassword,
                                                     HttpServletRequest request) {
        return userService.updateUserForUsers(userRequestWithoutPassword, request);
    }
    @GetMapping("/getUserByName") // http://localhost:8080/user/getUserByName?name=user1 + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<UserResponse> getUserByName(@RequestParam (name = "name") String userName) {
        return userService.getUserByName(userName);
    }












}

