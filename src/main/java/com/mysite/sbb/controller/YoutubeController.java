package com.mysite.sbb.controller;

import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.entity.Youtube;
import com.mysite.sbb.form.AnswerForm;
import com.mysite.sbb.form.YoutubeForm;
import com.mysite.sbb.service.UserService;
import com.mysite.sbb.service.YoutubeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/youtube")
@Controller
public class YoutubeController {

    private final YoutubeService youtubeService;
    private final UserService userService;

    @GetMapping("/list")
    public String youtube_list(Model model, @RequestParam(value = "page", defaultValue ="0") int page,
                                    @RequestParam(value ="kw",defaultValue = "") String kw) {
        log.info("page:{}, kw:{}", page, kw);
        Page<Youtube> paging = this.youtubeService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "youtube_list";
    }


    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Youtube youtube = this.youtubeService.getYoutube(id);
        model.addAttribute("youtube", youtube);
        return "youtube_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String YoutubeCreate(YoutubeForm youtubeForm) {
        return "youtube_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String YoutubeCreate(@Valid YoutubeForm youtubeForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "youtube_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.youtubeService.create(youtubeForm.getSubject(), youtubeForm.getYoutube_id(), youtubeForm.getContent(), siteUser);
        return "redirect:/youtube/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String youtubeModify(YoutubeForm youtubeForm, @PathVariable("id") Integer id, Principal principal) {
        Youtube youtube = this.youtubeService.getYoutube(id);
        if (!youtube.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        youtubeForm.setSubject(youtube.getSubject());
        youtubeForm.setContent(youtube.getContent());
        return "youtube_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String youtubeModify(@Valid YoutubeForm youtubeForm, BindingResult bindingResult, Principal principal,
                                 @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "youtube_form";
        }
        Youtube youtube = this.youtubeService.getYoutube(id);
        if (!youtube.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.youtubeService.modify(youtube, youtubeForm.getSubject(), youtubeForm.getYoutube_id(), youtubeForm.getContent());
        return String.format("redirect:/youtube/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String youtubeDelete(Principal principal, @PathVariable("id") Integer id) {
        Youtube youtube = this.youtubeService.getYoutube(id);
        if (!youtube.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.youtubeService.delete(youtube);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String youtubeVote(Principal principal, @PathVariable("id") Integer id) {
        Youtube youtube = this.youtubeService.getYoutube(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.youtubeService.vote(youtube, siteUser);
        return String.format("redirect:/youtube/detail/%s", id);
    }
}
