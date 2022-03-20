package com.example.algoproject.study.service;

import com.example.algoproject.errors.SuccessResponse;
import com.example.algoproject.errors.exception.NotExistStudyException;
import com.example.algoproject.errors.exception.NotExistUserException;
import com.example.algoproject.study.domain.BelongsTo;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.dto.*;
import com.example.algoproject.study.repository.BelongsToRepository;
import com.example.algoproject.study.repository.StudyRepository;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final BelongsToRepository belongsToRepository;

    @Transactional
    public String create(CustomUserDetailsVO cudVO, String repoName) {

        User leader = userRepository.findByUserId(cudVO.getUsername()).orElseThrow(NotExistUserException::new);
        log.info("study name: " + repoName);
        log.info("leader name: " + leader.getName());

        // 팀장의 github 이름으로 repoName 이 이름인 레포지토리 생성
        Map<String, Object> response = createRepositoryResponse(leader, repoName);

        studyRepository.save(new Study(response.get("id").toString(), response.get("name").toString(), cudVO.getUsername(), response.get("html_url").toString()));

        // 스터디 생성시 팀장을 스터디 멤버에 추가
        belongsToRepository.save(new BelongsTo(leader, studyRepository.findByStudyId(response.get("id").toString()).get(), true));

        return response.get("id").toString();
    }

    @Transactional
    public SuccessResponse addMember(CustomUserDetailsVO cudVO, AddMemberRequest request) {

        User leader = userRepository.findByUserId(cudVO.getUsername()).orElseThrow(NotExistUserException::new);
        log.info("leader name: " + leader.getName());

        User member = userRepository.findByName(request.getMemberName()).orElseThrow(NotExistUserException::new);
        log.info("member name: " + member.getName());

        Study study = studyRepository.findByName(request.getRepoName()).orElseThrow(NotExistStudyException::new);
        log.info("study name: " + study.getName());

        // leader 가 github 에서 member 에게 study 레포지토리로 contributor 초대를 보냄
        addContributorResponse(leader, member, study);

        belongsToRepository.save(new BelongsTo(member, study, false));

        return SuccessResponse.of(HttpStatus.OK, "멤버에게 스터디에 추가되었습니다.");
    }

    @Transactional
    public List<MemberInfoResponse> getMembers(MemberListRequest request) {

        User owner = userRepository.findByName(request.getOwnerName()).orElseThrow(NotExistUserException::new);
        log.info("owner name: " + owner.getName());

        Study study = studyRepository.findByName(request.getRepoName()).orElseThrow(NotExistStudyException::new);
        log.info("study name: " + study.getName());

        // 스터디에 있는 사람들 중 아직 초대 받지 않은 사람이 있으면 github 에서 다시 갱신해옴
        // 만약 다 초대를 받았다면 github 에서 갱신해오지 않는다
        List<BelongsTo> belongs = belongsToRepository.findByStudy(study);

        if (!isAllAccepted(belongs))
            updateMemberList(owner, study, belongs);

        return getMemberList(belongs);
    }

    @Transactional
    public StudyInfoResponse detail(String studyId) {

        Study study = studyRepository.findByStudyId(studyId).orElseThrow(NotExistStudyException::new);

        List<BelongsTo> belongs = belongsToRepository.findByStudy(study);

        List<MemberInfoResponse> members = getMemberList(belongs);

        return new StudyInfoResponse(study.getName(), study.getRepositoryUrl(), members);
    }

    @Transactional
    public List<Study> list(CustomUserDetailsVO cudVO) {

        User user = userRepository.findByUserId(cudVO.getUsername()).orElseThrow(NotExistUserException::new);

        List<BelongsTo> belongs = belongsToRepository.findByMember(user);

        return getStudyList(belongs);
    }

    //
    // private methods
    //

    private Map<String, Object> createRepositoryResponse(User leader, String repoName) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + leader.getAccessToken());
        headers.add("Accept", "application/vnd.github.v3+json");

        CreateRepositoryRequest request = new CreateRepositoryRequest();
        request.setName(repoName);
        request.setAuto_init(true);

        HttpEntity<CreateRepositoryRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                });

        // request 가 정상적으로 수행되지 않았을 때
        if(response.getBody() == null || response.getBody().get("id") == null)
            throw new RuntimeException("repository 생성을 실패했습니다.");

        return response.getBody();
    }

    private void addContributorResponse(User leader, User member, Study study) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github.v3+json");
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + leader.getAccessToken());

        AddContributorRequest request = new AddContributorRequest();
        request.setPermission("admin");

        HttpEntity<AddContributorRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + leader.getName() + "/" + study.getName() + "/collaborators/" + member.getName(),
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                });

        // request 가 정상적으로 수행되지 않았을 때
        if(response.getBody() == null || response.getBody().get("id") == null)
            throw new RuntimeException("contributor 초대를 실패했습니다.");
    }

    private List<Map<String, Object>> getContributorsResponse(User owner, Study study) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + owner.getAccessToken());
        headers.add("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + owner.getName() + "/" + study.getName() + "/collaborators",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });

        // request 가 정상적으로 수행되지 않았을 때
        if (response.getBody() == null || response.getBody().isEmpty())
            throw new RuntimeException("contributor 조회에 실패했습니다.");

        return response.getBody();
    }

    private boolean isAllAccepted(List<BelongsTo> belongs) {
        for (BelongsTo belongsTo : belongs)
            if(belongsTo.isAccepted())
                return false;
        return true;
    }

    private void updateMemberList(User owner, Study study, List<BelongsTo> belongs) {
        List<Map<String, Object>> responses = getContributorsResponse(owner, study);

        for (Map<String, Object> response : responses)
            for (BelongsTo belongsTo : belongs)
                // github api 를 이용해 조회한 contributor 가 새로 추가된 경우(초대를 받은 경우) 상태를 업데이트 해준다
                if(response.get("id") == belongsTo.getMember().getUserId() && !belongsTo.isAccepted()){
                    belongsTo.acceptInvitation();
                    belongsToRepository.save(belongsTo);
                }
    }

    private List<MemberInfoResponse> getMemberList(List<BelongsTo> belongs) {
        List<MemberInfoResponse> members = new ArrayList<>();

        for (BelongsTo belongsTo : belongs){
            User user = userRepository.findByUserId(belongsTo.getMember().getUserId()).orElseThrow(NotExistUserException::new);
            members.add(new MemberInfoResponse(user.getName(), user.getImageUrl(), belongsTo.isAccepted()));
        }

        return members;
    }

    private List<Study> getStudyList(List<BelongsTo> belongs) {

        List<Study> studyList = new ArrayList<>();

        for (BelongsTo belongsTo : belongs)
            studyList.add(belongsTo.getStudy());

        return studyList;
    }
}
