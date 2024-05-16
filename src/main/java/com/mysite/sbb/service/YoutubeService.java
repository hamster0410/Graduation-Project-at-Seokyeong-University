package com.mysite.sbb.service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.entity.Q_Answer;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.entity.Youtube;
import com.mysite.sbb.repository.YoutubeRepository;
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
public class YoutubeService {
    private final YoutubeRepository youtubeRepository;

    @SuppressWarnings("unused")
    private Specification<Youtube> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Youtube> y, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true); // 중복을 제거
                Join<Youtube, SiteUser> u1 = y.join("author", JoinType.LEFT);
                Join<Youtube, Q_Answer> a = y.join("answerList", JoinType.LEFT);
                Join<Q_Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(y.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(y.get("content"), "%" + kw + "%"), // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"), // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"), // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%")); // 답변 작성자
            }
        };
    }

    public Page<Youtube> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.youtubeRepository.findAllByKeyword(kw, pageable);
    }

    public Youtube getYoutube(Integer id) {
        Optional<Youtube> youtube = this.youtubeRepository.findById(id);
        if (youtube.isPresent()) {
            return youtube.get();
        } else {
            throw new DataNotFoundException("youtube not found");
        }
    }

    public void create(String subject, String youtube_id, String content, SiteUser user) {
        Youtube y = new Youtube();
        y.setSubject(subject);
        y.setYoutube_id(youtube_id);
        y.setContent(content);
        y.setCreateDate(LocalDateTime.now());
        y.setAuthor(user);
        this.youtubeRepository.save(y);
    }

    public void modify(Youtube youtube, String subject, String youtube_id, String content) {
        youtube.setSubject(subject);
        youtube.setYoutube_id(youtube_id);
        youtube.setContent(content);
        youtube.setModifyDate(LocalDateTime.now());
        this.youtubeRepository.save(youtube);
    }

    public void delete(Youtube youtube) {
        this.youtubeRepository.delete(youtube);
    }

    public void vote(Youtube youtube, SiteUser siteUser) {
        youtube.getVoter().add(siteUser);
        this.youtubeRepository.save(youtube);
    }
}
