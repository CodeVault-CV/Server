package com.example.algoproject.study.service;

import com.example.algoproject.belongsto.service.BelongsToService;
import com.example.algoproject.errors.exception.AlreadyExistMemberException;
import com.example.algoproject.errors.exception.FailedResponseException;
import com.example.algoproject.errors.exception.NotExistStudyException;
import com.example.algoproject.belongsto.domain.BelongsTo;
import com.example.algoproject.errors.exception.NotLeaderUserException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.dto.request.*;
import com.example.algoproject.study.dto.response.MemberInfo;
import com.example.algoproject.study.dto.response.StudyInfo;
import com.example.algoproject.study.repository.StudyRepository;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.service.UserService;
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
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserService userService;
    private final BelongsToService belongsToService;
    private final ResponseService responseService;

    @Transactional
    public CommonResponse create(CustomUserDetailsVO cudVO, CreateStudy request) {

        // 이름 중복 검사 예외처리 추가해야함

        User leader = userService.findByUserId(cudVO.getUsername());
        log.info("study name: " + request.getStudyName());
        log.info("repository name: " + request.getRepoName());
        log.info("leader name: " + leader.getName());

        // 팀장의 github 이름으로 repoName 이 이름인 레포지토리 생성
        Map<String, Object> response = createRepositoryResponse(leader, request.getRepoName());

        Study study = new Study(response.get("id").toString(), request.getStudyName(), cudVO.getUsername(), response.get("name").toString(), response.get("html_url").toString());

        studyRepository.save(study);

        // 스터디 생성시 팀장을 스터디 멤버에 추가
        belongsToService.save(new BelongsTo(leader, study, true));

        return responseService.getSingleResponse(response.get("id").toString());
    }

    @Transactional
    public CommonResponse addMember(CustomUserDetailsVO cudVO, AddMember request) {

        User leader = userService.findByUserId(cudVO.getUsername());
        log.info("leader name: " + leader.getName());

        User member = userService.findByName(request.getMemberName());
        log.info("member name: " + member.getName());

        Study study = studyRepository.findByStudyId(request.getStudyId()).orElseThrow(NotExistStudyException::new);
        log.info("study name: " + study.getName());

        // 이미 존재 중인 멤버 인지 확인 하는 것 추가
        List<BelongsTo> members = belongsToService.findByStudy(study);
        for (BelongsTo belongsTo : members)
            if (belongsTo.getMember().getId().equals(member.getId()))
                throw new AlreadyExistMemberException();

        // leader 가 github 에서 member 에게 study 레포지토리로 contributor 초대를 보냄
        addContributorResponse(leader, member, study);

        // Study 와 Member 간의 관계 저장
        belongsToService.save(new BelongsTo(member, study, false));

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse getMembers(String studyId) {

        Study study = getStudy(studyId);
        log.info("study name: " + study.getName());

        User owner = userService.findByUserId(study.getLeaderId());
        log.info("owner name: " + owner.getName());

        // 스터디에 있는 사람들 중 아직 초대 받지 않은 사람이 있으면 github 에서 다시 갱신해옴
        // 만약 다 초대를 받았다면 github 에서 갱신해오지 않는다
        List<BelongsTo> belongs = belongsToService.findByStudy(study);

        if (!isAllAccepted(belongs))
            updateMemberList(owner, study, belongs);

        return responseService.getListResponse(getMemberList(belongs));
    }

    @Transactional
    public CommonResponse detail(String studyId) {

        Study study = getStudy(studyId);
        User leader = userService.findByUserId(study.getLeaderId());

        if (synchronizeRepository(leader, study))
            return responseService.getErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 스터디 입니다.");

        List<BelongsTo> belongs = belongsToService.findByStudy(study);

        List<MemberInfo> members = getMemberList(belongs);

        return responseService.getSingleResponse(new StudyInfo(study.getName(), study.getRepositoryUrl(), members));
    }

    @Transactional
    public CommonResponse list(CustomUserDetailsVO cudVO) {

        User user = userService.findByUserId(cudVO.getUsername());
        List<BelongsTo> belongs = belongsToService.findByMember(user);
        List<String> list = new ArrayList<>();

        for (Study study : getStudyList(belongs))
            list.add(study.getStudyId());

        for (String studyId : list) {
            Study study = getStudy(studyId);
            User leader = userService.findByUserId(study.getLeaderId());

            // Github에 Repository가 존재하지 않으면 Database에서 삭제후 존재하지 않는 스터디 리스트에서 제외
            synchronizeRepository(leader, study);
        }

        return responseService.getListResponse(getStudyList(belongsToService.findByMember(user)));
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, String studyId) {

        Study study = getStudy(studyId);
        User leader = userService.findByUserId(study.getLeaderId());

        // Github에 Repository가 존재하지 않으면 Database에서 삭제후 존재하지 않는 스터디 예외
        if (synchronizeRepository(leader, study))
            return responseService.getErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 스터디 입니다.");

        // 스터디의 리더만 스터디를 삭제할 수 있음
        if (!cudVO.getUsername().equals(study.getLeaderId()))
            throw new NotLeaderUserException();

        // Github에 있는 레포지토리 삭제
        deleteRepositoryResponse(leader, study);

        // 먼저 Study에 연관된 BelongsTo들을 삭제 후 스터디 삭제
        belongsToService.deleteByStudy(study);
        studyRepository.delete(study);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public Study getStudy(String studyId) {
        return studyRepository.findByStudyId(studyId).orElseThrow(NotExistStudyException::new);
    }

    @Transactional
    public void save(Study study) {
        studyRepository.save(study);
    }

    @Transactional
    public Study findByStudyId(String studyId) {
        return studyRepository.findByStudyId(studyId).orElseThrow(NotExistStudyException::new);
    }

    //
    // private methods
    //

    private Map<String, Object> createRepositoryResponse(User owner, String repoName) {

        HttpHeaders headers = makeHeader(owner);

        CreateRepository request = new CreateRepository();
        request.setName(repoName);
        request.setAuto_init(true);

        HttpEntity<CreateRepository> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                });

        // request 가 정상적으로 수행되지 않았을 때
        if(!response.getStatusCode().is2xxSuccessful())
            throw new FailedResponseException("github api에서 repository 생성을 실패했습니다.");

        return response.getBody();
    }

    private void deleteRepositoryResponse(User owner, Study study) {

        HttpHeaders headers = makeHeader(owner);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + owner.getName() + "/" + study.getRepositoryName(),
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {
                });

        // request 가 정상적으로 수행되지 않았을 때
        if (!response.getStatusCode().is2xxSuccessful())
            throw new FailedResponseException("github api에서 repository 삭제를 실패했습니다.");

        log.info("repository " + study.getRepositoryName() + " deleted on github");
    }

    private void addContributorResponse(User owner, User member, Study study) {

        HttpHeaders headers = makeHeader(owner);

        AddContributor request = new AddContributor();
        request.setPermission("admin");

        HttpEntity<AddContributor> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + owner.getName() + "/" + study.getRepositoryName() + "/collaborators/" + member.getName(),
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                });

        // request 가 정상적으로 수행되지 않았을 때
        if(!response.getStatusCode().is2xxSuccessful())
            throw new FailedResponseException("github api에서 contributor 초대를 실패했습니다.");
    }

    private List<Map<String, Object>> getContributorsResponse(User owner, Study study) {

        HttpHeaders headers = makeHeader(owner);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + owner.getName() + "/" + study.getRepositoryName() + "/collaborators",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });

        // request 가 정상적으로 수행되지 않았을 때
        if (!response.getStatusCode().is2xxSuccessful())
            throw new FailedResponseException("github api에서 contributor 조회에 실패했습니다.");

        return response.getBody();
    }

    private boolean synchronizeRepository(User owner, Study study) {

        HttpHeaders headers = makeHeader(owner);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/users/" + owner.getName() + "/repos",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });

        if (!response.getStatusCode().is2xxSuccessful())
            throw new FailedResponseException("github api에서 repository 조회에 실패했습니다.");

        for (Map<String, Object> map : Objects.requireNonNull(response.getBody()))
            if (map.get("id").toString().equals(study.getStudyId()))
                return false;

        // 먼저 Study에 연관된 BelongsTo들을 삭제 후 스터디 삭제
        belongsToService.deleteByStudy(study);
        studyRepository.delete(study);

        return true;
    }

    private HttpHeaders makeHeader(User owner) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + owner.getAccessToken());
        headers.add("Accept", "application/vnd.github.v3+json");
        return headers;
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
                if(response.get("id") == belongsTo.getMember().getId() && !belongsTo.isAccepted()){
                    belongsTo.acceptInvitation();
                    belongsToService.save(belongsTo);
                }
    }

    private List<MemberInfo> getMemberList(List<BelongsTo> belongs) {
        List<MemberInfo> members = new ArrayList<>();

        for (BelongsTo belongsTo : belongs){
            User user = belongsTo.getMember();
            members.add(new MemberInfo(user.getName(), user.getImageUrl(), belongsTo.isAccepted()));
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
