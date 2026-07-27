package com.myapp.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // = @Configuration + @EnableAutoConfiguration + @ComponentScan
public class TaskmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerApplication.class, args);
		// Esto: arranca Tomcat embebido, escanea Beans, configura todo
	}

}
