package com.example.WoorworkingForum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class WoorworkingForumApplication {

	public static void main(String[] args) {
		SpringApplication.run(WoorworkingForumApplication.class, args);
	}

}
