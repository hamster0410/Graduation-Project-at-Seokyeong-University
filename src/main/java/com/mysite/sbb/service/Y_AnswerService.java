package com.mysite.sbb.service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.entity.*;
import com.mysite.sbb.repository.Q_AnswerRepository;
import com.mysite.sbb.repository.Y_AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class Y_AnswerService {

	private final Y_AnswerRepository YAnswerRepository;

	public Y_Answer create(Youtube youtube, String content, SiteUser author) {
		Y_Answer YAnswer = new Y_Answer();
		YAnswer.setContent(content);
		YAnswer.setCreateDate(LocalDateTime.now());
		YAnswer.setYoutube(youtube);
		YAnswer.setAuthor(author);
		this.YAnswerRepository.save(YAnswer);
		return YAnswer;
	}

	public Y_Answer getAnswer(Integer id) {
		Optional<Y_Answer> answer = this.YAnswerRepository.findById(id);
		if (answer.isPresent()) {
			return answer.get();
		} else {
			throw new DataNotFoundException("answer not found");
		}
	}

	public void modify(Y_Answer YAnswer, String content) {
		YAnswer.setContent(content);
		YAnswer.setModifyDate(LocalDateTime.now());
		this.YAnswerRepository.save(YAnswer);
	}

	public void delete(Y_Answer YAnswer) {
		this.YAnswerRepository.delete(YAnswer);
	}

	public void vote(Y_Answer YAnswer, SiteUser siteUser) {
		YAnswer.getVoter().add(siteUser);
		this.YAnswerRepository.save(YAnswer);
	}
}
