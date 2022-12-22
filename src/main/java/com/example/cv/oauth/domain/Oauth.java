package com.example.cv.oauth.domain;

public interface Oauth {
    /**
     * @return 액세스 토큰 요청을 위한 코드를 요청하는 URL
     */
    String getOauthRedirectURL();

    /**
     * 액세스 토큰 요청을 위한 code 로 액세스 토큰을 반환하는 메소드
     *
     * @param code 액세스 토큰 요청을 위한 코드
     * @return 액세스 토큰 객체
     */
    OauthToken getAccessToken(String code);

    /**
     * 액세스 토큰으로 사용자 정보를 요청해 각 소셜 서버에서 사용자 정보를 가져와 ID를 반환하는 메소드
     *
     * @param token 소셜 로그인 서비스 액세스 토큰 객체
     * @return 각 소셜 서비스에서 제공받은 ID를 반환
     */
    String getUserInfo(OauthToken token);
}
