package com.example.cv.oauth.service;

import com.example.cv.oauth.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.cv.oauth.domain.OauthType.*;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final GoogleOauth googleOauth;
    private final KakaoOauth kakaoOauth;
    private final NaverOauth naverOauth;
    private final GithubOauth githubOauth;
    private final HttpServletResponse response;

    /**
     * 각 소셜 로그인 서비스에 액세스 토큰을 받기위한 코드를 요청하는 메소드
     *
     * @param type 소셜 로그인 타입
     */
    public void request(OauthType type) {
        Oauth oauth = mapOauth(type);
        try {
            response.sendRedirect(oauth.getOauthRedirectURL());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 각 소셜 로그인 서비스에서 제공받은 코드로 액세스 토큰을 요청하여 받은 후 사용자의 정보를 가져와 이후 처리를 하는 메소드
     *
     * @param type 소셜 로그인 타입
     * @param code 액세스 토큰 요청을 위한 코드
     */
    public void login(OauthType type, String code) {
        Oauth oauth = mapOauth(type);
        OauthToken accessToken = oauth.getAccessToken(code);
        String userInfoResponse = oauth.getUserInfo(accessToken);

        // TODO
        // 가져온 ID를 가지고 사용자 정보를 조회해
        // DB에 있다면 액세스 토큰을 업데이트하고 메인페이지로 이동(임시)
        // DB에 없다면 새로운 사용자 생성 화면으로 이동 (화면을 이동하고 해당 사용자가 생성되었을 때와 중간에 그만둘 때의 경우를 생각해야 한다)
    }

    /**
     * 각 소셜 로그인 타입에 해당 되는 Oauth 객체를 반환하는 메서드
     *
     * @param type 소셜 로그인 타입
     * @return 로그인 타입에 해당되는 Oauth 객체
     */
    private Oauth mapOauth(OauthType type) {
        if (type.equals(GOOGLE)) {
            return this.googleOauth;
        }
        if (type.equals(KAKAO)) {
            return this.kakaoOauth;
        }
        if (type.equals(NAVER)) {
            return this.naverOauth;
        }
        if (type.equals(GITHUB)) {
            return this.githubOauth;
        }
        return null;
    }
}
