package com.project.service;
import com.project.entity.concretes.user.User;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.user.UserRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.UserResponse;
import com.project.repository.UserRepository;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    //username uniq mi(db de kontrol) 2- password hashleme 3-rolü ne?
    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {

        //!!! username-ssn-phoneNumber-email unique mi?

        uniquePropertyValidator.checkDuplicate(userRequest.getUsername(),
                userRequest.getSsn(),userRequest.getPhoneNumber(),
                userRequest.getEmail());

        //!!! DTO-->POJO
        User user = userMapper.mapUserRequestToUser(userRequest);
        //!!!Rol bilgisi
        if (userRole.equalsIgnoreCase(RoleType.ADMIN.name())){ //bestpractice

            if(Objects.equals(userRequest.getUsername(), "SuperAdmin")) {
                user.setBuilt_in(true);
            }
            user.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
        } else if (userRole.equalsIgnoreCase("Dean")){

            user.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
        } else if (userRole.equalsIgnoreCase("ViceDean")) {

            user.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));
        } else {
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_USERROLE_MESSAGE, userRole));
        }
        //!!! password encode edilecek
       // user.setPassword(passwordEncoder.encode(user.getPassword())); ikisi de olur
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        //!!! Advisor durumu False yapılıyor
        user.setIsAdvisor(Boolean.FALSE); //nullar gelemez
        User savedUser = userRepository.save(user);
        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_CREATE)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();
    }

    public Page<UserResponse> getUserByPage(int page, int size, String sort, String type, String userRole) {

    }
}
