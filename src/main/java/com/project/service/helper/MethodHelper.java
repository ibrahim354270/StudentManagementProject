package com.project.service.helper;

import com.project.entity.concretes.user.User;
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
    //!!! built In kontrolu
    public void checkBuiltIn(User user){
        if(Boolean.TRUE.equals(user.getBuilt_in())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }
}













