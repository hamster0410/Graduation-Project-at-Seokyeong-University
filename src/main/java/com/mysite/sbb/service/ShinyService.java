package com.mysite.sbb.service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.entity.Q_Answer;
import com.mysite.sbb.entity.Question;
import com.mysite.sbb.entity.ShinyApp;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.repository.ShinyAppRepository;
import com.mysite.sbb.repository.UserRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShinyService {
    private final ShinyAppRepository shinyAppRepository;
    private final UserRepository userRepository;
    private Specification<ShinyApp> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ShinyApp> s, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true); // 중복을 제거
                Join<Question, SiteUser> u1 = s.join("author", JoinType.LEFT);
                return cb.or(cb.like(s.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(s.get("content"), "%" + kw + "%"), // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"));
            }
        };
    }
    public Page<ShinyApp> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.shinyAppRepository.findAllByKeyword(kw, pageable);
    }

    public ShinyApp getShinyApp(String subject,String username) {
        Optional<SiteUser> user = this.userRepository.findByusername(username);
        Optional<ShinyApp> shinyApp = Optional.ofNullable(this.shinyAppRepository.findBySubjectAndAuthor(subject,user));
        if (shinyApp.isPresent()) {
            return shinyApp.get();
        } else {
            throw new DataNotFoundException("shinyapp not found");
        }
    }

    public void create(String subject, String content, SiteUser user) {
        ShinyApp s = new ShinyApp();
        s.setSubject(subject);
        s.setContent(content);
        s.setCreateDate(LocalDateTime.now());
        s.setAuthor(user);
        this.shinyAppRepository.save(s);
    }

    public void delete(ShinyApp shinyApp) {
        this.shinyAppRepository.delete(shinyApp);
    }

    public void vote(ShinyApp shinyApp, SiteUser siteUser) {
        shinyApp.getVoter().add(siteUser);
        this.shinyAppRepository.save(shinyApp);
    }
}
