package com.project.repository;

import com.project.entity.concretes.user.User;
import com.project.payload.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameEquals(String username);

    //!!! alttaki ve ustteki ayni isi yapiyor
    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.userRole.roleName = :roleName") //JPQL // ?1 ile :roleName aynı
    //join işlemi arka planda bizim için yapılıyor.nesneler üzerinden ilişkiler belirlendi bunu kendi çözüyor

    Page<User> findByUserByRole(String roleName, Pageable pageable);//String userRole, Pageable pageable
}










