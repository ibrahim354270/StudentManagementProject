package com.project.service;
import com.project.entity.concretes.user.User;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.repository.UserRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;
    private final MethodHelper methodHelper;

    //username uniq mi(db de kontrol) 2- password hashleme 3-rolü ne?
    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {

        //!!! username-ssn-phoneNumber-email unique mi?

        uniquePropertyValidator.checkDuplicate(userRequest.getUsername(),
                userRequest.getSsn(), userRequest.getPhoneNumber(),
                userRequest.getEmail());

        //!!! DTO-->POJO
        User user = userMapper.mapUserRequestToUser(userRequest);
        //!!!Rol bilgisi
        if (userRole.equalsIgnoreCase(RoleType.ADMIN.name())) { //bestpractice

            if (Objects.equals(userRequest.getUsername(), "SuperAdmin")) {
                user.setBuilt_in(true);
            }
            user.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
        } else if (userRole.equalsIgnoreCase("Dean")) {

            user.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
        } else if (userRole.equalsIgnoreCase("ViceDean")) {

            user.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));
        } else {
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_USERROLE_MESSAGE, userRole));
        }
        //!!! password encode edilecek
        // user.setPassword(passwordEncoder.encode(user.getPassword())); ikisi de olur
        user.setPassword(passwordEncoder.encode(userRequest.getPassword())); //hash kayıt işlemi yapılacak
        //!!! Advisor durumu False yapılıyor
        user.setIsAdvisor(Boolean.FALSE); //nullar gelemez
        User savedUser = userRepository.save(user);

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_CREATE)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();
    }

    public Page<UserResponse> getUserByPage(int page, int size, String sort, String type, String userRole) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return userRepository.findByUserByRole(userRole, pageable).map(userMapper::mapUserToUserResponse);
        //mapten önce yazdığımız değeleri alır argüman olarak dto gönderir

    }

    public ResponseMessage<BaseUserResponse> getUserById(Long userId) {
        BaseUserResponse baseUserResponse = null; //daha sonra içine response gelecek
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));

        if (user.getUserRole().getRoleType() == RoleType.STUDENT) {
            baseUserResponse = userMapper.mapUserToStudentResponse(user);
        } else if (user.getUserRole().getRoleType() == RoleType.TEACHER) {
            baseUserResponse = userMapper.mapUserToTeacherResponse(user);
        } else {
            baseUserResponse = userMapper.mapUserToUserResponse(user);
        }

        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_FOUND)
                .status(HttpStatus.OK)
                .object(baseUserResponse)
                .build();

    }

    public String deleteUserById(Long id, HttpServletRequest request) {

        //!!! silinecek user var mi kontrolu
        User user = methodHelper.isUserExist(id); // silinmesini istedigim user
        //!!! silinme talebinde bulunan user
        String userName = (String) request.getAttribute("username");
        User user2 = userRepository.findByUsername(userName); // silinme talebinde bulunan user
        //!!! built in kontrolu
        if (Boolean.TRUE.equals(user.getBuilt_in())) {//null kontrolu yapmış oluyoruz
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            // !!! Mudur sadece Teacher,Student veya Mudur Yrd. silebilsin
        } else if (user2.getUserRole().getRoleType() == RoleType.MANAGER) {
            if (!((user.getUserRole().getRoleType() == RoleType.TEACHER) ||
                    (user.getUserRole().getRoleType() == RoleType.STUDENT) ||
                    (user.getUserRole().getRoleType() == RoleType.ASSISTANT_MANAGER))) {
                throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            }
            //!!! Mdr Yrdc si sadece --> Teacher ve Student silebilsin
        } else if (user2.getUserRole().getRoleType() == RoleType.ASSISTANT_MANAGER) {
            if (!((user.getUserRole().getRoleType() == RoleType.TEACHER) ||
                    (user.getUserRole().getRoleType() == RoleType.STUDENT))) {
                throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            }
        }

        userRepository.deleteById(id);
        return SuccessMessages.USER_DELETE;
    }

    public ResponseMessage<BaseUserResponse> updateUser(UserRequest userRequest, Long userId) {
        //!!! var mi yok mu kontrolu
        User user = methodHelper.isUserExist(userId);

        //!!! builIn kontrolu
        methodHelper.checkBuiltIn(user);

        //!!! unique kontrolu
        uniquePropertyValidator.checkUniqueProperties(user, userRequest);

        //!!! DTO --> POJO
        User updatedUser = userMapper.mapUserRequestToUpdatedUser(userRequest, userId);

        //!!! Password encode
        updatedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        //!!! Rol setleniyor
        updatedUser.setUserRole(user.getUserRole());

        User savedUser = userRepository.save(updatedUser);
        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_UPDATE_MESSAGE)
                .status(HttpStatus.OK)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();
    }

    public ResponseEntity<String> updateUserForUsers(UserRequestWithoutPassword userRequest,
                                                     HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsername(userName);
        //!!! builtIn kontrol
        methodHelper.checkBuiltIn(user);
        //!!! unique kontrol
        uniquePropertyValidator.checkUniqueProperties(user, userRequest);
        //!!! DTO --> POJO //mapleye bilirdik bu halini görmek için yazdık
        user.setUsername(userRequest.getUsername());
        user.setBirthDay(userRequest.getBirthDay());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setBirthPlace(userRequest.getBirthPlace());
        user.setGender(userRequest.getGender());
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setSsn(userRequest.getSsn());

        userRepository.save(user);

        String message = SuccessMessages.USER_UPDATE_MESSAGE;

        return ResponseEntity.ok(message);
    }

    public List<UserResponse> getUserByName(String name) {

        return  userRepository.getUserByNameContaining(name)
                .stream()
                .map(userMapper::mapUserToUserResponse)
                .collect(Collectors.toList());
    }
    public long countAllAdmins() {
        return userRepository.countAdmin(RoleType.ADMIN); //JPQL
    }
}

















