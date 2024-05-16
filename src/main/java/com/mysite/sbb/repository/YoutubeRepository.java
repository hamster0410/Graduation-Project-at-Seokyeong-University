package com.mysite.sbb.repository;

import com.mysite.sbb.entity.Youtube;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface YoutubeRepository extends JpaRepository<Youtube, Integer> {
    Youtube findBySubject(String subject);

    Youtube findBySubjectAndContent(String subject, String content);

    List<Youtube> findBySubjectLike(String subject);

    Page<Youtube> findAll(Pageable pageable);

    Page<Youtube> findAll(Specification<Youtube> spec, Pageable pageable);

//    @Query("select "
//            + "distinct q "
//            + "from Question q "
//            + "left outer join SiteUser u1 on q.author=u1 "
//            + "left outer join Answer a on a.question=q "
//            + "left outer join SiteUser u2 on a.author=u2 "
//            + "where "
//            + "   q.subject like %:kw% "
//            + "   or q.content like %:kw% "
//            + "   or u1.username like %:kw% "
//            + "   or a.content like %:kw% "
//            + "   or u2.username like %:kw% ")
//    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

    @Query("select "
            + "distinct y "
            + "from Youtube y "
            + "left outer join SiteUser u1 on y.author=u1 "
            + "left outer join Y_Answer a on a.youtube=y "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   y.subject like %:kw% "
            + "   or y.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% ")
    Page<Youtube> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
