package com.mysite.sbb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Component;


@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class})
@EnableRedisHttpSession
public class SbbApplication {


	public static void main(String[] args) {
		SpringApplication.run(SbbApplication.class, args);
	}

}
