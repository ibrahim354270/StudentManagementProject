package com.project.service.user;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.user.StudentRequest;
import com.project.payload.request.user.StudentRequestWithoutPassword;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.user.StudentResponse;
import com.project.repository.UserRepository;
import com.project.service.UserRoleService;
import com.project.service.helper.MethodHelper;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final MethodHelper methodHelper;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    public ResponseMessage<StudentResponse> saveStudent(StudentRequest studentRequest) {

        User advisorTeacher = methodHelper.isUserExist(studentRequest.getAdvisorTeacherId());

        methodHelper.checkAdvisor(advisorTeacher);

        uniquePropertyValidator.checkDuplicate(studentRequest.getUsername(),
                studentRequest.getSsn(),
                studentRequest.getPhoneNumber(),
                studentRequest.getEmail());

        User student = userMapper.mapStudentRequestToUser(studentRequest);

        student.setAdvisorTeacherId(advisorTeacher.getId());
        student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        student.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        student.setActive(true);
        student.setIsAdvisor(Boolean.FALSE);
        student.setStudentNumber(getLastNumber());

        return ResponseMessage.<StudentResponse>builder()
                .object(userMapper.mapUserToStudentResponse(userRepository.save(student)))
                .message(SuccessMessages.STUDENT_SAVE)
                .build();
    }

    private int getLastNumber(){

        if(!userRepository.findStudent(RoleType.STUDENT)){
            return 1000;
        }
        return userRepository.getMaxStudentNumber() + 1 ;
    }

    public ResponseEntity<String> updateStudent(StudentRequestWithoutPassword studentRequest, HttpServletRequest request) {

        String userName = (String) request.getAttribute("username");
        User student = userRepository.findByUsername(userName);

        uniquePropertyValidator.checkUniqueProperties(student, studentRequest);

        student.setMotherName(studentRequest.getMotherName());
        student.setFatherName(studentRequest.getFatherName());
        student.setBirthDay(studentRequest.getBirthDay());
        student.setEmail(studentRequest.getEmail());
        student.setPhoneNumber(studentRequest.getPhoneNumber());
        student.setBirthPlace(studentRequest.getBirthPlace());
        student.setGender(studentRequest.getGender());
        student.setName(studentRequest.getName());
        student.setSurname(studentRequest.getSurname());
        student.setSsn(studentRequest.getSsn());

        userRepository.save(student);

        String message = SuccessMessages.STUDENT_UPDATE;
        return ResponseEntity.ok(message);
    }

    public ResponseMessage<StudentResponse> updateStudentForManagers(Long userId, StudentRequest studentRequest) {
        //!!! id var mi ??
        User user = methodHelper.isUserExist(userId);
        //!!! student mi ??
        methodHelper.checkRole(user, RoleType.STUDENT);
        //!!! unique kontrolu
        uniquePropertyValidator.checkUniqueProperties(user, studentRequest);

        User studentForUpdate = userMapper.mapStudentRequestToUpdatedUser(studentRequest, userId);
        studentForUpdate.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        //TODO : AdvisorTeacherId  bilgisi gercekten Advisora mi ait
        studentForUpdate.setAdvisorTeacherId(studentRequest.getAdvisorTeacherId());
        studentForUpdate.setStudentNumber(user.getStudentNumber());
        studentForUpdate.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        studentForUpdate.setActive(true);

        return ResponseMessage.<StudentResponse>builder()
                .object(userMapper.mapUserToStudentResponse(userRepository.save(studentForUpdate)))
                .message(SuccessMessages.STUDENT_UPDATE)
                .status(HttpStatus.OK)
                .build();
    }

    public ResponseMessage changeStatusOfStudent(Long id, boolean status) {
        User student = methodHelper.isUserExist(id);
        methodHelper.checkRole(student,RoleType.STUDENT);

        student.setActive(status); //istersek if ile statu kontrolu yapabiliriz
        userRepository.save(student);//Merge

        return ResponseMessage.builder()
                .message("Student is " + (status ? "active" : "passive"))
                .status(HttpStatus.OK)
                .build();
    }
}