package com.example.algoproject.user.service;

import com.example.algoproject.errors.exception.notfound.NotExistUserException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.github.service.GithubService;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.LoginDto;
import com.example.algoproject.user.repository.UserRepository;
import com.example.algoproject.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final GithubService githubService;
    private final ResponseService responseService;
    private final JWTUtil jwtUtil;

    @Transactional
    public CommonResponse login(String code) {

        // {code} 를 이용해 Github 에 access_token 요청
        String token = githubService.getAccessToken(code);

        // 받은 access_token 으로 Github 에 사용자 정보 요청
        Map<String, Object> userInfo = githubService.getUserInfo(token);

        userRepository.findById(userInfo.get("id").toString())
                .ifPresentOrElse(user -> {
                    log.info(user.getName() + " User already exists. Renew User Name & Access Token...");
                    // 유저의 이름과 프로필 사진이 변경되었을 수도 있기 때문에 accessToken 과 같이 갱신해 준다
                    user.update(token, userInfo.get("login").toString(), userInfo.get("avatar_url").toString());
                    userRepository.save(user);
                }, () -> {
                    log.info("Add new user to database... " + userInfo.get("login"));
                    userRepository.save(new User(userInfo.get("id").toString(), userInfo.get("login").toString(), token, userInfo.get("avatar_url").toString()));
                });

        return responseService.getSingleResponse(new LoginDto(jwtUtil.makeJWT(userInfo.get("id").toString()), userInfo.get("id").toString()));
    }



    @Transactional(readOnly = true)
    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(NotExistUserException::new);
    }

    @Transactional(readOnly = true)
    public User findByName(String name) {
        return userRepository.findByName(name).orElseThrow(NotExistUserException::new);
    }

    @Transactional(readOnly = true)
    public List<User> findByNameContains(String name) {
        return userRepository.findByNameContains(name);
    }
}
