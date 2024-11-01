package com.backend.wordswap.domain.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://localhost:3000", "http://localhost:3001", "https://107.21.147.148", "http://107.21.147.148", 
				"https://wordswap-frontend.vercel.app", "https://bk.wordswap.tech", "https://wordswap.tech/", "http://3.218.163.10:3001")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
				.allowCredentials(true).maxAge(3600);
	}
}
