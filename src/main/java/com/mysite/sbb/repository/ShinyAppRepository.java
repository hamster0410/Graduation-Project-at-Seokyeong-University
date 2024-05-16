package com.mysite.sbb.repository;

import com.mysite.sbb.entity.ShinyApp;
import com.mysite.sbb.entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShinyAppRepository extends JpaRepository<ShinyApp, Integer> {
    ShinyApp findBySubject(String subject);

    ShinyApp findBySubjectAndAuthor(String subject, Optional<SiteUser> author);

    ShinyApp findBySubjectAndContent(String subject, String content);

    List<ShinyApp> findBySubjectLike(String subject);

    Page<ShinyApp> findAll(Pageable pageable);

    Page<ShinyApp> findAll(Specification<ShinyApp> spec, Pageable pageable);

    @Query("select "
            + "distinct s "
            + "from ShinyApp s "
            + "left outer join SiteUser u1 on s.author=u1 "
            + "where "
            + "   s.subject like %:kw% "
            + "   or s.content like %:kw% "
            + "   or u1.username like %:kw% ")
            Page<ShinyApp> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
