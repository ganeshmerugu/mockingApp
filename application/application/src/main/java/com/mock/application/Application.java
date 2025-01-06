package com.mock.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.io.IOException;
import java.net.ServerSocket;

@SpringBootApplication

public class Application {





	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
