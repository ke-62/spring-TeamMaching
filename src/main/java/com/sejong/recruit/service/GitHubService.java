package com.sejong.recruit.service;

import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GitHub API 연동 서비스
 * GitHub 프로필 및 레포지토리 정보를 가져옴
 */
@Service
public class GitHubService {

    private static final String GITHUB_API_URL = "https://api.github.com";
    private final RestTemplate restTemplate;

    public GitHubService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * GitHub 사용자 프로필 조회
     * @param username GitHub 사용자 이름
     */
    public GitHubProfile getProfile(String username) {
        try {
            String url = GITHUB_API_URL + "/users/" + username;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                throw new RuntimeException("GitHub 사용자를 찾을 수 없습니다");
            }

            return GitHubProfile.builder()
                    .login((String) response.get("login"))
                    .name((String) response.get("name"))
                    .bio((String) response.get("bio"))
                    .avatarUrl((String) response.get("avatar_url"))
                    .publicRepos((Integer) response.get("public_repos"))
                    .followers((Integer) response.get("followers"))
                    .following((Integer) response.get("following"))
                    .htmlUrl((String) response.get("html_url"))
                    .build();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("GitHub 사용자를 찾을 수 없습니다: " + username);
        } catch (Exception e) {
            throw new RuntimeException("GitHub API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * GitHub 레포지토리 목록 조회
     * @param username GitHub 사용자 이름
     * @param limit 가져올 레포지토리 개수
     */
    public List<GitHubRepository> getRepositories(String username, int limit) {
        try {
            String url = GITHUB_API_URL + "/users/" + username + "/repos?sort=updated&per_page=" + limit;
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

            if (response == null) {
                return new ArrayList<>();
            }

            List<GitHubRepository> repositories = new ArrayList<>();
            for (Map<String, Object> repo : response) {
                repositories.add(GitHubRepository.builder()
                        .name((String) repo.get("name"))
                        .description((String) repo.get("description"))
                        .htmlUrl((String) repo.get("html_url"))
                        .language((String) repo.get("language"))
                        .stargazersCount((Integer) repo.get("stargazers_count"))
                        .forksCount((Integer) repo.get("forks_count"))
                        .isPrivate((Boolean) repo.get("private"))
                        .build());
            }

            return repositories;
        } catch (Exception e) {
            throw new RuntimeException("GitHub 레포지토리 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * GitHub URL에서 사용자 이름 추출
     * @param githubUrl GitHub 프로필 URL
     */
    public String extractUsername(String githubUrl) {
        if (githubUrl == null || githubUrl.isEmpty()) {
            throw new RuntimeException("GitHub URL이 비어있습니다");
        }

        // https://github.com/username 형식에서 username 추출
        String[] parts = githubUrl.replace("https://", "")
                .replace("http://", "")
                .replace("github.com/", "")
                .split("/");

        if (parts.length == 0 || parts[0].isEmpty()) {
            throw new RuntimeException("유효하지 않은 GitHub URL입니다");
        }

        return parts[0];
    }

    /**
     * GitHub 프로필 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GitHubProfile {
        private String login;
        private String name;
        private String bio;
        private String avatarUrl;
        private Integer publicRepos;
        private Integer followers;
        private Integer following;
        private String htmlUrl;
    }

    /**
     * GitHub 레포지토리 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GitHubRepository {
        private String name;
        private String description;
        private String htmlUrl;
        private String language;
        private Integer stargazersCount;
        private Integer forksCount;
        private Boolean isPrivate;
    }
}
