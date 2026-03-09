package com.sejong.recruit.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubService {

    private final RestTemplate restTemplate;

    public GitHubProfile getProfile(String githubUrl) {
        String username = extractUsername(githubUrl);
        if (username == null) return null;

        try {
            String apiUrl = "https://api.github.com/users/" + username;
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            if (response == null) return null;

            return new GitHubProfile(
                    (String) response.get("login"),
                    (String) response.get("name"),
                    (String) response.get("bio"),
                    (String) response.get("avatar_url"),
                    (Integer) response.getOrDefault("public_repos", 0),
                    (Integer) response.getOrDefault("followers", 0),
                    (Integer) response.getOrDefault("following", 0),
                    (String) response.get("html_url")
            );
        } catch (Exception e) {
            log.error("GitHub 프로필 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<GitHubRepository> getRepositories(String githubUrl, int limit) {
        String username = extractUsername(githubUrl);
        if (username == null) return Collections.emptyList();

        try {
            String apiUrl = "https://api.github.com/users/" + username + "/repos?sort=updated&per_page=" + limit;
            List<Map<String, Object>> response = restTemplate.getForObject(apiUrl, List.class);
            if (response == null) return Collections.emptyList();

            return response.stream()
                    .map(repo -> new GitHubRepository(
                            (String) repo.get("name"),
                            (String) repo.get("description"),
                            (String) repo.get("html_url"),
                            (String) repo.get("language"),
                            (Integer) repo.getOrDefault("stargazers_count", 0),
                            (Integer) repo.getOrDefault("forks_count", 0),
                            (Boolean) repo.getOrDefault("private", false)
                    ))
                    .toList();
        } catch (Exception e) {
            log.error("GitHub 레포지토리 조회 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String extractUsername(String githubUrl) {
        if (githubUrl == null || githubUrl.isBlank()) return null;
        Pattern pattern = Pattern.compile("github\\.com/([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(githubUrl);
        return matcher.find() ? matcher.group(1) : null;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GitHubProfile {
        private String login;
        private String name;
        private String bio;
        private String avatarUrl;
        private int publicRepos;
        private int followers;
        private int following;
        private String htmlUrl;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GitHubRepository {
        private String name;
        private String description;
        private String htmlUrl;
        private String language;
        private int stargazersCount;
        private int forksCount;
        private boolean isPrivate;
    }
}
