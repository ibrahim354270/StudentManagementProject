package com.project.service.helper;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MethodHelper {

    private final UserRepository userRepository;

    //!! id ile kontrol

    public User isUserExist(Long userId){
        return userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));
    }
    //!!! isUserExistByUsername
    public User isUserExistByUsername(String username){
        User user =  userRepository.findByUsernameEquals(username);
        if(user.getId() == null) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE_WITH_USERNAME,username));
        }
        return user;
    }

    //!!! built In kontrolu
    public void checkBuiltIn(User user){
        if(Boolean.TRUE.equals(user.getBuilt_in())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }
    //!!! Rol kontrolu yapan method
    public void checkRole(User user, RoleType roleType){
        if(!user.getUserRole().getRoleType().equals(roleType)){
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.NOT_FOUND_USER_WITH_ROLE_MESSAGE, user.getId(), roleType));
        }
    }
    //!!! Gelen User Advicor degilse exception firlatan yardimci metod
    public void checkAdvisor(User user){
        if(Boolean.FALSE.equals(user.getIsAdvisor())){
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVISOR_MESSAGE, user.getId()));
        }
    }

}













