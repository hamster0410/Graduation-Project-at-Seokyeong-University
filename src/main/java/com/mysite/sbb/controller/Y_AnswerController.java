package com.mysite.sbb.controller;

import com.mysite.sbb.entity.*;
import com.mysite.sbb.form.AnswerForm;
import com.mysite.sbb.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/YAnswer")
@RequiredArgsConstructor
@Controller
public class Y_AnswerController {

	private final YoutubeService youtubeService;
	private final Y_AnswerService YAnswerService;
	private final UserService userService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm,
			BindingResult bindingResult, Principal principal) {
		Youtube youtube = this.youtubeService.getYoutube(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		if (bindingResult.hasErrors()) {
			model.addAttribute("youtube", youtube);
			return "youtube_detail";
		}
		Y_Answer YAnswer = this.YAnswerService.create(youtube, answerForm.getContent(), siteUser);
		return String.format("redirect:/youtube/detail/%s#answer_%s", YAnswer.getYoutube().getId(), YAnswer.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
		Y_Answer YAnswer = this.YAnswerService.getAnswer(id);
		if (!YAnswer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		answerForm.setContent(YAnswer.getContent());
		return "answer_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "answer_form";
		}
		Y_Answer YAnswer = this.YAnswerService.getAnswer(id);
		if (!YAnswer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		this.YAnswerService.modify(YAnswer, answerForm.getContent());
		return String.format("redirect:/youtube/detail/%s#answer_%s", YAnswer.getYoutube().getId(), YAnswer.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
		Y_Answer YAnswer = this.YAnswerService.getAnswer(id);
		if (!YAnswer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		this.YAnswerService.delete(YAnswer);
		return String.format("redirect:/youtube/detail/%s", YAnswer.getYoutube().getId());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String answerVote(Principal principal, @PathVariable("id") Integer id) {
		Y_Answer YAnswer = this.YAnswerService.getAnswer(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		this.YAnswerService.vote(YAnswer, siteUser);
		return String.format("redirect:/youtube/detail/%s#answer_%s", YAnswer.getYoutube().getId(), YAnswer.getId());
	}
}
