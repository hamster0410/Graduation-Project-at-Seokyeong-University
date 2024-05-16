package com.mysite.sbb.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class SiteUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Column(nullable = false) // null 값을 허용하지 않음
	private boolean hasContainer = false; // 기본값을 false로 설정

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<ShinyApp> shinyApps;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<Question> questions;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<Q_Answer> qAnswers;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<Y_Answer> yAnswers;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<Youtube> youtube;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<R_Container> r_container;

	@Column(unique = true)
	private String email;
}
