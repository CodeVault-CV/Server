package com.example.algoproject.errors.response;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {

    // 성공 여부만 전달하는 응답
    public CommonResponse getSuccessResponse() {
        CommonResponse response = new CommonResponse();
        setSuccessResponse(response);
        return response;
    }

    // 단일 데이터를 전달하는 응답
    public <T> SingleResponse<T> getSingleResponse(T data) {
        SingleResponse response = new SingleResponse();
        response.data = data;
        setSuccessResponse(response);
        return response;
    }

    // 여러 데이터를 전달하는 응답
    public <T> ListResponse<T> getListResponse(List<T> data) {
        ListResponse<T> response = new ListResponse<>();
        response.data = data;
        setSuccessResponse(response);
        return response;
    }

    // 예외를 전달하는 응답
    public CommonResponse getErrorResponse(int status, String message) {
        CommonResponse response = new CommonResponse();
        response.status = status;
        response.message = message;
        return response;
    }

    void setSuccessResponse(CommonResponse response) {
        response.status = HttpStatus.OK.value();
        response.message = "SUCCESS";
    }
}
