package com.project.contactMessage.repository;

import com.project.contactMessage.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage,Long> {

    Page<ContactMessage> findByEmailEquals(String email, Pageable pageable);

    Page<ContactMessage> findBySubjectEquals(String subject, Pageable pageable);

    //Function(postgreSql) ile yapabiliyoruz ama dinamik olmuyor db değişmesi durumunda sorun olur
    @Query("SELECT c FROM ContactMessage c WHERE FUNCTION('DATE', c.dateTime) BETWEEN ?1 and ?2")
    List<ContactMessage> findMessagesBetweenDates(LocalDate beginDate, LocalDate endDate);

    //Extract kullanırsak db değişse bile sorun olmaz, datetime dan sadece time kısmını getiriyoruz
    //bizim istediğimiz sorgu için 3 tane kontrol var
    @Query("SELECT c FROM ContactMessage c WHERE " +
            //1-benim saatim başlangıç ile bitiş arasında mı?
            "(EXTRACT(HOUR FROM c.dateTime) BETWEEN :startH AND :endH) AND " +
            //2-benim saatim başlangıç saatine eşit değil mi? veya dakikam başlangıç dakikasına eşit veya büyük mü?
            "(EXTRACT(HOUR FROM c.dateTime) != :startH OR (EXTRACT(MINUTE FROM c.dateTime) >= :startM) AND " +
            //3-benim saatim bitiş saatine eşit değil mi? veya dakikam bitiş dakikasına eşit veya küçük mü?
            "(EXTRACT(HOUR FROM c.dateTime) != :endH OR (EXTRACT(MINUTE FROM c.dateTime) <= :endM)")
    List<ContactMessage> findMessagesBetweenTimes(@Param("startH") int startH, @Param("startM") int startM,
                                                  @Param("endH") int endH, @Param("endM") int endM);


}


