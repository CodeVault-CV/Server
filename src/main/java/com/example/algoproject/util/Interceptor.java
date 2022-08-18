package com.example.algoproject.util;

import com.example.algoproject.security.JWTUtil;
import com.example.algoproject.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@RequiredArgsConstructor
@Component
public class Interceptor implements HandlerInterceptor {

    private final JWTUtil jwtUtil;
    private final StudyService studyService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("url : {}", request.getRequestURI() + " " + request.getMethod());

        if (!(handler instanceof HandlerMethod))
            return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null)
            return true;

        String userId = jwtUtil.getJWTId(jwtUtil.resolveToken(request));
        String studyId = request.getHeader("Study");

        if (auth.role().equals(Auth.Role.LEADER))
            studyService.validateLeader(studyId, userId);

        if (auth.role().equals(Auth.Role.MEMBER))
            studyService.validateMember(studyId, userId);

        if (auth.role().equals(Auth.Role.OWN)) {

        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("response status: {}", response.getStatus());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
