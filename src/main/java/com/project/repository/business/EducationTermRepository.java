package com.project.repository.business;
import com.project.entity.concretes.business.EducationTerm;
import com.project.entity.enums.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EducationTermRepository extends JpaRepository<EducationTerm, Long> {

    @Query("SELECT (count(e) > 0) FROM EducationTerm e WHERE e.term=?1 AND EXTRACT(YEAR FROM e.startDate) =?2")//extract=bul çıkar
        //term=paramatre year eşit mi  ve s.Date içinden yıl tarihini al bu değer int=year'a eşit mi. ve böyle bir sonucun sayısı 0'dan büyük  ise True dönücek.
        //true döndüğü zaman bizde onu if içinde hata mesajı veriyoruz.
    boolean existsByTermAndYear(Term term, int year);//tabloda year diye değişken yk.S.date içinden çekiyoruz.bu yüzden türetilmiş değil,jpql

    @Query("SELECT e FROM EducationTerm e WHERE EXTRACT(YEAR FROM e.startDate) = ?1")
    List<EducationTerm> findByYear(int year);
}
