package com.sejong.recruit.domain.auth.service;

import com.sejong.recruit.domain.auth.dto.SejongAuthDto;
import com.sejong.recruit.common.exception.BusinessException;
import com.sejong.recruit.common.exception.ErrorCode;
import com.sejong.recruit.security.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 세종대학교 포털 인증 서비스 (고전독서 사이트 경유 방식)
 */
@Slf4j
@Service
public class SejongPortalAuthService {

    private final OkHttpClient client;
    private final JwtProvider jwtProvider;

    private static final String PORTAL_BASE_URL = "https://portal.sejong.ac.kr";
    private static final String LOGIN_URL = PORTAL_BASE_URL + "/jsp/login/login_action.jsp";
    private static final String SSO_URL = "http://classic.sejong.ac.kr/_custom/sejong/sso/sso-return.jsp?returnUrl=https://classic.sejong.ac.kr/classic/index.do";
    private static final String STATUS_URL = "https://classic.sejong.ac.kr/classic/reading/status.do";

    public SejongPortalAuthService(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
        this.client = buildUnsafeClient();
    }

    /**
     * 포털 계정 정보를 통해 학생 인증을 수행합니다.
     */
    public SejongAuthDto.AuthResponse authenticate(String id, String pw) {
        try {
            executePortalLogin(id, pw);
            executeSsoRedirect();
            String html = fetchStatusPageHtml();

            return parseUserInfo(html);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Auth] 인증 중 예기치 않은 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PORTAL_COMMUNICATION_ERROR);
        }
    }

    /**
     * 세종대 포털 로그인 수행
     */
    private void executePortalLogin(String id, String pw) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("mainLogin", "N")
                .add("id", id)
                .add("password", pw)
                .build();

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(formBody)
                .header("Referer", PORTAL_BASE_URL)
                .header("User-Agent", "Mozilla/5.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            validateLoginResponse(response);
        }
    }

    private void validateLoginResponse(Response response) throws IOException {
        String body = response.body() != null ? response.body().string() : "";
        if (body.contains("fail") || body.contains("불일치") || body.contains("존재하지 않는")) {
            throw new BusinessException(ErrorCode.PORTAL_LOGIN_FAILED);
        }
    }

    /**
     * SSO 세션 전이 수행
     */
    private void executeSsoRedirect() throws IOException {
        Request request = new Request.Builder().url(SSO_URL).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BusinessException(ErrorCode.PORTAL_COMMUNICATION_ERROR);
            }
        }
    }

    /**
     * 정보 페이지 HTML 획득
     */
    private String fetchStatusPageHtml() throws IOException {
        Request request = new Request.Builder().url(STATUS_URL).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null || response.code() != 200) {
                throw new BusinessException(ErrorCode.PORTAL_COMMUNICATION_ERROR);
            }
            return response.body().string();
        }
    }

    /**
     * HTML 파싱 및 DTO 변환
     */
    private SejongAuthDto.AuthResponse parseUserInfo(String html) {
        Document doc = Jsoup.parse(html);
        String selector = ".b-con-box:has(h4:contains(사용자 정보)) table.b-board-table tbody tr";
        List<String> values = new ArrayList<>();

        doc.select(selector).forEach(tr -> values.add(tr.select("td").text().trim()));

        if (values.size() < 5) {
            log.error("[Auth] 파싱 데이터 부족: {}", values.size());
            throw new BusinessException(ErrorCode.PORTAL_PARSING_ERROR);
        }

        String studentId = values.get(1);

        return SejongAuthDto.AuthResponse.builder()
                .major(values.get(0))
                .studentId(studentId)
                .name(values.get(2))
                .grade(values.get(3).replaceAll("[^0-9]", ""))
                .status(values.get(4))
                .completedSemester(values.size() > 5 ? values.get(5).replaceAll("[^0-9]", "") : null)
                .accessToken(jwtProvider.createAccessToken(studentId))
                .refreshToken(jwtProvider.createRefreshToken(studentId))
                .build();
    }

    /**
     * 보안 검증을 우회하는 Unsafe 클라이언트 생성 (개발 편의성)
     */
    private OkHttpClient buildUnsafeClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .followRedirects(true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("HTTP 클라이언트 초기화 실패", e);
        }
    }
}
