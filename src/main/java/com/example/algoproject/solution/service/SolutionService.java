package com.example.algoproject.solution.service;

import com.example.algoproject.errors.SuccessResponse;
import com.example.algoproject.errors.exception.NotExistUserException;
import com.example.algoproject.s3.S3Uploader;
import com.example.algoproject.solution.dto.CommitFileRequest;
import com.example.algoproject.solution.repository.SolutionRepository;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.repository.UserRepository;
import com.example.algoproject.util.PathUtil;
import com.example.algoproject.util.ReadMeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class SolutionService {

    private final UserRepository userRepository;
    private final SolutionRepository solutionRepository;
    private final S3Uploader s3Uploader;
    private final PathUtil pathUtil;
    private final ReadMeUtil readMeUtil;

    public SuccessResponse upload(CustomUserDetailsVO cudVO, MultipartFile code,
                                  String header, String content, String time, String memory) throws IOException {

        User user = userRepository.findByUserId(cudVO.getUsername()).orElseThrow(NotExistUserException::new);
//        String folderPath = "problemNo/Johoseong";
        String gitHubPath = pathUtil.makeGitHubPath("corpName", code.getOriginalFilename(), "probName", user.getName());
        String s3Path = pathUtil.makeS3Path("repoName", "corpName", code.getOriginalFilename(), "probName", user.getName());

        /* readme file 생성 메소드 */
        MultipartFile readMe = readMeUtil.makeReadMe(header, content);

        /* github에 file commit */
        checkFileResponse(code, user, gitHubPath, "BOJ_algorithm_study", "");
        checkFileResponse(readMe, user, gitHubPath, "BOJ_algorithm_study", "");

        /* s3에 file upload */
        String s3CodeUrl = s3Uploader.upload(code, s3Path); // s3에 저장
//        String s3ReadMeUrl = s3Uploader.upload(readMe, s3Path + )
        log.info("s3 path : " + s3Path);

        /* local 리드미 삭제 */
        readMeUtil.removeReadMe(readMe);

        return SuccessResponse.of(HttpStatus.OK, "코드와 리드미 파일이 정상적으로 업로드 되었습니다..");
    }

//    public String getCodeFile(CustomUserDetailsVO cudVO, String problemNo) throws IOException {
//
//        User user = userRepository.findByUserId(cudVO.getUsername()).orElseThrow(NotExistUserException::new);
//        Solution solution = solutionRepository.findByUserIdAndProblemNo(user, problemNo).orElseThrow(NotExistUserException::new);
//
//        String folder = solution.getProblemNo(); // 문제번호 == 폴더명
//        String fileName = solution.getFileName(); // uuid + 파일명
//
//        return s3Uploader.readS3(folder, fileName);
//    }

    /*
    private method
    */

    private void checkFileResponse(MultipartFile multipartFile, User user, String path, String repoName, String commitMessage) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github.v3+json");
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + user.getAccessToken());

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity entity = new HttpEntity<>(headers); // http entity에 header 담아줌
        try { // 깃허브에 파일 존재.
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://api.github.com/repos/" + user.getName() + "/" + repoName + "/contents/" + path + multipartFile.getOriginalFilename(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });

            log.info("get sha : " + response.getBody().get("sha").toString());

            commitFileResponse(response.getBody().get("sha").toString(), multipartFile, user, path, repoName, commitMessage);
        } catch (HttpClientErrorException e) { // 깃허브에 파일 존재 x. 새로 생성되는 파일인 경우 404 에러 뜸.
            commitFileResponse(null, multipartFile, user, path, repoName, commitMessage);
        }
    }

    /* github file commit 메소드 */
    private Map<String, Object> commitFileResponse(String sha, MultipartFile multipartFile, User user, String path, String repoName, String commitMessage) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github.v3+json");
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + user.getAccessToken());

        CommitFileRequest request = new CommitFileRequest();
        request.setMessage(commitMessage);
        request.setContent(Base64.getEncoder().encodeToString(multipartFile.getBytes())); // 내용 base64로 인코딩 해줘야됨 (필수)

        if (sha != null) { // 기존 파일 수정하는 거면 sha 바디에 추가해야됨
            request.setSha(sha);
        }

        HttpEntity<CommitFileRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + user.getName() + "/" + repoName + "/contents/" + path + multipartFile.getOriginalFilename(),
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                });
        log.info("github path : " + path + multipartFile.getOriginalFilename());

        return response.getBody();
    }

}
