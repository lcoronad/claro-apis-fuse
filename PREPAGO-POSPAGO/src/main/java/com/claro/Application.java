package com.claro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author Assert Solutions S.A.S
 */
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

	/**
	 * A main method to start this application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	
}
