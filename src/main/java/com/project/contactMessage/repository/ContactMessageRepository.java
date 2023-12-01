package com.project.contactMessage.repository;

import com.project.contactMessage.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage,Long> {

    Page<ContactMessage> findByEmailEquals(String email, Pageable pageable);
    Page<ContactMessage> findBySubjectEquals(String subject, Pageable pageable);
}