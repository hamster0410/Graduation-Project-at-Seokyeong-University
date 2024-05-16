package com.mysite.sbb.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.mysite.sbb.entity.Q_Answer;
import com.mysite.sbb.repository.Q_AnswerRepository;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.entity.Question;
import com.mysite.sbb.entity.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class Q_AnswerService {

	private final Q_AnswerRepository QAnswerRepository;

	public Q_Answer create(Question question, String content, SiteUser author) {
		Q_Answer QAnswer = new Q_Answer();
		QAnswer.setContent(content);
		QAnswer.setCreateDate(LocalDateTime.now());
		QAnswer.setQuestion(question);
		QAnswer.setAuthor(author);
		this.QAnswerRepository.save(QAnswer);
		return QAnswer;
	}

	public Q_Answer getAnswer(Integer id) {
		Optional<Q_Answer> answer = this.QAnswerRepository.findById(id);
		if (answer.isPresent()) {
			return answer.get();
		} else {
			throw new DataNotFoundException("answer not found");
		}
	}

	public void modify(Q_Answer QAnswer, String content) {
		QAnswer.setContent(content);
		QAnswer.setModifyDate(LocalDateTime.now());
		this.QAnswerRepository.save(QAnswer);
	}

	public void delete(Q_Answer QAnswer) {
		this.QAnswerRepository.delete(QAnswer);
	}

	public void vote(Q_Answer QAnswer, SiteUser siteUser) {
		QAnswer.getVoter().add(siteUser);
		this.QAnswerRepository.save(QAnswer);
	}
}
