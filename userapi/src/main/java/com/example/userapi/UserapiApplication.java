package com.example.userapi;

import com.example.userapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@EnableCaching
@EnableRetry
@SpringBootApplication
public class UserapiApplication implements CommandLineRunner {
	private final UserService userService;

	@Autowired
	public UserapiApplication(UserService userService) {
		this.userService = userService;
	}

	public static void main(String[] args) {
		SpringApplication.run(UserapiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userService.loadUsersFromExternalApi();
	}

}
