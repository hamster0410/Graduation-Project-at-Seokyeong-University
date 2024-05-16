package com.mysite.sbb.repository;

import java.util.Optional;

import com.mysite.sbb.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
	Optional<SiteUser> findByusername(String username);
}
