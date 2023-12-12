package com.project.service;

import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import com.project.payload.messages.ErrorMessages;
import com.project.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService { //bir method sadece service katında var ise başka bir servise hizmet etmek için
    //bir service class ında method dto değilde pojo dönüyorsa: bu method u başka bir service de çağırdığımız için
    //yada kötü bir kod olur o yüzden :)

    private final UserRoleRepository userRoleRepository;

    public UserRole getUserRole(RoleType roleType){
        return userRoleRepository.findByEnumRoleEquals(roleType).orElseThrow(
                ()-> new ResolutionException(ErrorMessages.ROLE_NOT_FOUND)
        );
    }
    public List<UserRole> getAllUserRoles(){
        return userRoleRepository.findAll();
    }
}













