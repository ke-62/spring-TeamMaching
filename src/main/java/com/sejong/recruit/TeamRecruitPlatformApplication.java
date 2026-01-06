package com.sejong.recruit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TeamRecruitPlatformApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TeamRecruitPlatformApplication.class, args);
    }
}
