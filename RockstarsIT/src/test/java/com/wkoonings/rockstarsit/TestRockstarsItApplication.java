package com.wkoonings.rockstarsit;

import org.springframework.boot.SpringApplication;

public class TestRockstarsItApplication {

	public static void main(String[] args) {
		SpringApplication.from(RockstarsItApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
