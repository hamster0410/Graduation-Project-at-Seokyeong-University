package com.mysite.sbb.controller;

import java.security.Principal;

import com.mysite.sbb.entity.Q_Answer;
import com.mysite.sbb.form.AnswerForm;
import com.mysite.sbb.service.Q_AnswerService;
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

import com.mysite.sbb.entity.Question;
import com.mysite.sbb.service.QuestionService;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/QAnswer")
@RequiredArgsConstructor
@Controller
public class Q_AnswerController {

	private final QuestionService questionService;
	private final Q_AnswerService QAnswerService;
	private final UserService userService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm,
			BindingResult bindingResult, Principal principal) {
		Question question = this.questionService.getQuestion(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		if (bindingResult.hasErrors()) {
			model.addAttribute("question", question);
			return "question_detail";
		}
		Q_Answer QAnswer = this.QAnswerService.create(question, answerForm.getContent(), siteUser);
		return String.format("redirect:/question/detail/%s#answer_%s", QAnswer.getQuestion().getId(), QAnswer.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
		Q_Answer QAnswer = this.QAnswerService.getAnswer(id);
		if (!QAnswer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		answerForm.setContent(QAnswer.getContent());
		return "answer_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "answer_form";
		}
		Q_Answer QAnswer = this.QAnswerService.getAnswer(id);
		if (!QAnswer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		this.QAnswerService.modify(QAnswer, answerForm.getContent());
		return String.format("redirect:/question/detail/%s#answer_%s", QAnswer.getQuestion().getId(), QAnswer.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
		Q_Answer QAnswer = this.QAnswerService.getAnswer(id);
		if (!QAnswer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		this.QAnswerService.delete(QAnswer);
		return String.format("redirect:/question/detail/%s", QAnswer.getQuestion().getId());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String answerVote(Principal principal, @PathVariable("id") Integer id) {
		Q_Answer QAnswer = this.QAnswerService.getAnswer(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		this.QAnswerService.vote(QAnswer, siteUser);
		return String.format("redirect:/question/detail/%s#answer_%s", QAnswer.getQuestion().getId(), QAnswer.getId());
	}
}
