package com.project.repository;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    @Query("SELECT r FROM UserRole r WHERE r.roleType = ?1") //aşağıda gelen rol tipi ile eşit olan
    Optional<UserRole> findByEnumRoleEquals(RoleType roleType);
}













