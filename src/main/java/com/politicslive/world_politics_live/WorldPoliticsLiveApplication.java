package com.politicslive.world_politics_live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.politicslive")
@EnableScheduling
public class WorldPoliticsLiveApplication {
	public static void main(String[] args) {
		SpringApplication.run(WorldPoliticsLiveApplication.class, args);
	}
}
