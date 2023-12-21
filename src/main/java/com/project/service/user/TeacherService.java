package com.project.service.user;

import com.project.entity.concretes.business.LessonProgram;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.exception.ConflictException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.ChooseLessonTeacherRequest;
import com.project.payload.request.user.TeacherRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.UserResponse;
import com.project.payload.response.user.StudentResponse;
import com.project.payload.response.user.TeacherResponse;
import com.project.repository.UserRepository;
import com.project.service.UserRoleService;
import com.project.service.business.LessonProgramService;
import com.project.service.helper.MethodHelper;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final MethodHelper methodHelper;
    private final LessonProgramService lessonProgramService;

    public ResponseMessage<TeacherResponse> saveTeacher(TeacherRequest teacherRequest) {
        //TODO : LessonProgram eklenecek

        //!!! unique kontrolu
        uniquePropertyValidator.checkDuplicate(teacherRequest.getUsername(), teacherRequest.getSsn(),
                teacherRequest.getPhoneNumber(), teacherRequest.getEmail());

        //!!! DTO --> POJO--dbye kayıt kısmında usera çevirmemiz gerek
        User teacher = userMapper.mapTeacherRequestToUser(teacherRequest);

        //!!! POJO da olmasi gerekipde DTO da olmayan verileri setliyoruz
        teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));//rol varsa gelicek servicen yoksa hata mesajı gönderecek
        //TODO : Lessonrogram eklenecek

        //!!! Password encode
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));

        if (teacherRequest.getIsAdvisorTeacher()) {//requestten
            teacher.setIsAdvisor(Boolean.TRUE);//Dbye kayıt kısmı boş kalmamalı
        } else teacher.setIsAdvisor(Boolean.FALSE);

        User savedTeacher = userRepository.save(teacher);

        return ResponseMessage.<TeacherResponse>builder()
                .message(SuccessMessages.TEACHER_SAVE)
                .status(HttpStatus.CREATED)
                .object(userMapper.mapUserToTeacherResponse(savedTeacher))
                .build();
    }

    public ResponseMessage<TeacherResponse> updateTeacherForManagers(TeacherRequest teacherRequest, Long userId) {
        //!!! id kontrol
        User user = methodHelper.isUserExist(userId);
        //!!! Parametrede gelen id, bir teacher a ait mi kontrolu
        methodHelper.checkRole(user, RoleType.TEACHER);
        //TODO : LessonProgram eklenecek
        //!!! unique kontrolu
        uniquePropertyValidator.checkUniqueProperties(user, teacherRequest);
        //!!! DTO --> POJO
        User updatedTeacher = userMapper.mapTeacherRequestToUpdatedUser(teacherRequest, userId);
        //!!! Password encode
        updatedTeacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword()));

        //TODO : LessonProgram

        updatedTeacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
        //put mappin aldığımız için hepsini güncellememiz gerekiyor

        User savedTeacher = userRepository.save(updatedTeacher);
        return ResponseMessage.<TeacherResponse>builder()
                .object(userMapper.mapUserToTeacherResponse(savedTeacher))
                .message(SuccessMessages.TEACHER_UPDATE)
                .status(HttpStatus.OK)
                .build();
    }


    public List<StudentResponse> getAllStudentByAdvisorUsername(String userName) {
        //!!! user kontrolu
        User teacher = methodHelper.isUserExistByUsername(userName);
        //!!! isAdvisor Kontrol
        methodHelper.checkAdvisor(teacher);
//AdvisorTeacherId ,student eklerken advisor ıd'side eklenecek,Teacher Id'si girerek ona ait olan öğrenci listesini görebiliriz.
        return userRepository.findByAdvisorTeacherId(teacher.getId())
                .stream()
                .map(userMapper::mapUserToStudentResponse) // Stream<StudentResponse>
                .collect(Collectors.toList());

    }


    public ResponseMessage<UserResponse> saveAdvisorTeacher(Long teacherId) {
        //!!! id'li User var mi kontrolu
        User teacher = methodHelper.isUserExist(teacherId);
        //!!! id ile gelen User, Teacher mi kontrolurrorMessages.
        methodHelper.checkRole(teacher, RoleType.TEACHER);
        //!!! id ile gelen Teacher, zaten Advisor mi kontrolu
        if (Boolean.TRUE.equals(teacher.getIsAdvisor())) {
            throw new ConflictException(
                    String.format(ErrorMessages.ALREADY_EXIST_ADVISOR_MESSAGE, teacherId));
        }
        teacher.setIsAdvisor(Boolean.TRUE);
        userRepository.save(teacher);

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.ADVISOR_TEACHER_SAVE)
                .object(userMapper.mapUserToUserResponse(teacher))
                .status(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<UserResponse> deleteAdvisorTeacherById(Long teacherId) {

        //!!! id var mi ?
        User teacher = methodHelper.isUserExist(teacherId);
        //!!! Teacher advisor mi ?
        methodHelper.checkRole(teacher, RoleType.TEACHER); // Optional
        methodHelper.checkAdvisor(teacher);
        teacher.setIsAdvisor(Boolean.FALSE);
        userRepository.save(teacher);

        //!!! silinen Advisor Teacher in rehberligindeki ogrencileri ile irtibatini kopariyoruz
        List<User> allStudents = userRepository.findByAdvisorTeacherId(teacherId);
        if (!allStudents.isEmpty()) {//liste doluysa
            allStudents.forEach(students -> students.setAdvisorTeacherId(null));
        }

        //TODO: meet??
        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.ADVISOR_TEACHER_DELETE)
                .object(userMapper.mapUserToUserResponse(teacher))
                .status(HttpStatus.OK)
                .build();
    }

    public List<UserResponse> getAllAdvisorTeacher() {

        return userRepository.findAllByAdvisor(Boolean.TRUE)
                .stream()
                .map(userMapper::mapUserToUserResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<TeacherResponse> addLessonProgram(ChooseLessonTeacherRequest chooseLessonTeacherRequest) {
        User teacher = methodHelper.isUserExist(chooseLessonTeacherRequest.getTeacherId());
        methodHelper.checkRole(teacher, RoleType.TEACHER);

        //eklenmesi istenen lesson programlar geldi
        Set<LessonProgram> lessonPrograms =
                lessonProgramService.getLessonProgramById(chooseLessonTeacherRequest.getLessonProgramId());

        //teacher in mevcuttaki lesson programları getirildi
        Set<LessonProgram> teachersLessonProgram = teacher.getLessonProgramList();
        //TODO ODEV  : cakisma kontrolu
        lessonConflict(lessonPrograms,teachersLessonProgram);
        teachersLessonProgram.addAll(lessonPrograms);//ekleme yaptık
        teacher.setLessonProgramList(teachersLessonProgram);

        User updatedTeacher = userRepository.save(teacher);

        return ResponseMessage.<TeacherResponse>builder()
                .message(SuccessMessages.LESSON_PROGRAM_ADD_TO_TEACHER)
                .status(HttpStatus.OK)
                .object(userMapper.mapUserToTeacherResponse(updatedTeacher))
                .build();
    }



    public  void lessonConflict(Set<LessonProgram> request, Set<LessonProgram> dB) {
        for (LessonProgram requestProgram : request) {
            for (LessonProgram dbProgram : dB) {
                // Aynı term mü?
                boolean sameTerm = requestProgram.getEducationTerm().equals(dbProgram.getEducationTerm());
                // Aynı gün mü?
                boolean sameDay = requestProgram.getDay().equals(dbProgram.getDay());
                // Start ve stop time eşit mi?
                boolean equalSST = requestProgram.getStartTime().equals(dbProgram.getStartTime())
                        && requestProgram.getStopTime().equals(dbProgram.getStopTime());
                // Start ve stopT arasında mı?
                boolean betweenSST = requestProgram.getStartTime().isAfter(dbProgram.getStartTime())
                        && requestProgram.getStopTime().isBefore(dbProgram.getStopTime());

                // Aynı term içine ve aynı günde ise
                if (sameTerm && sameDay && (equalSST || betweenSST)) {
                    throw new ConflictException(ErrorMessages.LESSON_PROGRAM_CONFLICT_MESSAGE);
                }
            }
        }
    }
}

//    public void lessonConflict(LessonProgram request, LessonProgram dB) {
//        //aynı term mü?
//        boolean sameTerm=request.getEducationTerm().equals(dB.getEducationTerm());
//        //aynı gün mü?
//        boolean sameDay=request.getDay().equals(dB.getDay());
//        //start ve stop time eşit mi
//        boolean equalSST=request.getStartTime().equals(dB.getStartTime())
//                & request.getStopTime().equals(dB.getStopTime());
//        //Start ve stopT arasında mı
//        boolean betweenSST=request.getStartTime().isAfter(dB.getStartTime())
//                & request.getStopTime().isBefore(dB.getStopTime());
//
//        // aynı term içine ve aynı Günde ise
//        if (sameTerm & sameDay & (equalSST || betweenSST))  {
//
//            throw new ConflictException(ErrorMessages.LESSON_PROGRAM_CONFLICT_MESSAGE);
//        }
//
//    }

