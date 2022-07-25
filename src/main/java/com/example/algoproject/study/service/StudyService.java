package com.example.algoproject.study.service;

import com.example.algoproject.belongsto.service.BelongsToService;
import com.example.algoproject.belongsto.domain.BelongsTo;
import com.example.algoproject.errors.exception.badrequest.AlreadyExistMemberException;
import com.example.algoproject.errors.exception.badrequest.AlreadyExistRepositoryNameException;
import com.example.algoproject.errors.exception.badrequest.SameNameException;
import com.example.algoproject.errors.exception.badrequest.SameUserException;
import com.example.algoproject.errors.exception.forbidden.NotLeaderUserException;
import com.example.algoproject.errors.exception.forbidden.StudyAuthException;
import com.example.algoproject.errors.exception.notfound.NotExistMemberException;
import com.example.algoproject.errors.exception.notfound.NotExistRepositoryException;
import com.example.algoproject.errors.exception.notfound.NotExistStudyException;
import com.example.algoproject.errors.response.CommonResponse;
import com.example.algoproject.errors.response.ResponseService;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.study.dto.request.*;
import com.example.algoproject.study.dto.response.StudyInfo;
import com.example.algoproject.study.dto.response.StudyListInfo;
import com.example.algoproject.study.repository.StudyRepository;
import com.example.algoproject.user.domain.User;
import com.example.algoproject.user.dto.CustomUserDetailsVO;
import com.example.algoproject.user.dto.UserInfo;
import com.example.algoproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${baseUrl}")
    private String url;

    private final StudyRepository studyRepository;
    private final UserService userService;
    private final BelongsToService belongsToService;
    private final ResponseService responseService;

    @Transactional
    public CommonResponse create(CustomUserDetailsVO cudVO, CreateStudy request) {

        User leader = userService.findById(cudVO.getUsername());
        log.info("study name: " + request.getStudyName());
        log.info("repository name: " + request.getRepoName());
        log.info("leader name: " + leader.getName());

        // 팀장의 github 이름으로 repoName 이 이름인 레포지토리 생성
        Map<String, Object> response = createRepositoryResponse(leader, request.getRepoName());

        // 스터디에 해당하는 Repository 의 webhook 생성
        createWebhookResponse(leader, request.getRepoName());

        Study study = new Study(response.get("id").toString(), request.getStudyName(), cudVO.getUsername(), response.get("name").toString(), response.get("html_url").toString());
        save(study);

        // 스터디 생성시 팀장을 스터디 멤버에 추가
        belongsToService.save(new BelongsTo(leader, study));

        return responseService.getSingleResponse(response.get("id").toString());
    }

    @Transactional
    public CommonResponse update(CustomUserDetailsVO cudVO, UpdateStudy request) {

        Study study = findById(request.getId());

        // 리더 유저인지 확인
        checkLeader(userService.findById(cudVO.getUsername()), study);

        // 스터디의 이름이 이전과 같음
        if (study.getName().equals(request.getName()))
            throw new SameNameException();

        // 스터디의 이름을 변경
        study.setName(request.getName());
        save(study);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse detail(CustomUserDetailsVO cudVO, String id) {

        User user = userService.findById(cudVO.getUsername());
        Study study = findById(id);
        List<User> members = getMembers(study);

        // 해당 스터디에 속한 사람인지 확인
        checkAuth(user, study);

        return responseService.getSingleResponse(new StudyInfo(study, getMemberInfos(members)));
    }

    @Transactional
    public CommonResponse list(CustomUserDetailsVO cudVO) {
        return responseService.getListResponse(getStudyInfos(userService.findById(cudVO.getUsername())));
    }

    @Transactional
    public CommonResponse delete(CustomUserDetailsVO cudVO, String id) {

        User user = userService.findById(cudVO.getUsername());
        Study study = findById(id);

        // 리더 유저인지 확인
        checkLeader(user, study);

        // github API 로 저장소 삭제 요청
        deleteRepositoryResponse(user, study);

        // 데이터베이스에서 스터디와 연관된 belongto들을 삭제
        belongsToService.deleteAllByStudy(study);
        studyRepository.delete(study);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse addMember(CustomUserDetailsVO cudVO, Member request) {

        User leader = userService.findById(cudVO.getUsername());
        User member = userService.findByName(request.getMember());
        Study study = findById(request.getStudyId());

        log.info("leader name: " + leader.getName());
        log.info("member name: " + member.getName());
        log.info("study name: " + study.getName());

        // 리더 유저인지 확인
        checkLeader(leader, study);

        // 자기 자신을 초대할 수 없음
        checkSame(leader, member);

        // 추가할 멤버가 이미 스터디에 있지않은지 확인
        try {
            checkMember(member, study);
            // NotMemberUser 에외가 발생하지 않으면 이미 존재하는 멤버로 예외처리
            throw new AlreadyExistMemberException();
        } catch (NotExistMemberException ex) {
            // leader 가 github 에서 member 에게 study 레포지토리로 contributor 초대를 보냄
            addContributorResponse(leader, member, study);
        }
        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse deleteMember(CustomUserDetailsVO cudVO, Member request) {

        User leader = userService.findById(cudVO.getUsername());
        User member = userService.findByName(request.getMember());
        Study study = findById(request.getStudyId());

        log.info("leader name: " + leader.getName());
        log.info("member name: " + member.getName());
        log.info("study name: " + study.getName());

        // 요청한 유저가 리더인지 확인
        checkLeader(leader, study);

        // 자기 자신을 삭제할 수 없음
        checkSame(leader, member);

        // 삭제할 멤버가 스터디에 속해있는지 확인
        checkMember(member, study);

        // leader 가 github 에서 member 에게 study 레포지토리로 contributor 초대를 보냄
        removeContributorResponse(leader, member, study);

        return responseService.getSuccessResponse();
    }

    @Transactional
    public CommonResponse searchMember(CustomUserDetailsVO cudVO, SearchUser request) {

        log.info("search user name: {}", request.getName());

        User leader = userService.findById(cudVO.getUsername());
        Study study = findById(request.getId());

        // 요청한 유저가 리더인지 확인
        checkLeader(leader, study);

        return responseService.getListResponse(findUserByNameContains(request.getName(), study));
    }

    @Transactional
    public void webhook(Map<String, Object> response) {

        // Webhook 이 왔을 때 경우(contributor, repository)를 분리하여 method 실행
        Map<String, Object> repoMap = (Map<String, Object>) response.get("repository");
        Study study = findById(repoMap.get("id").toString());

        // contributor
        if (response.containsKey("member")) {
            Map<String, Object> memberMap = (Map<String, Object>) response.get("member");
            User member = userService.findById(memberMap.get("id").toString());

            // contributor 추가됐을 때에 데이터베이스에 추가
            if (response.get("action").equals("added")) {
                log.info("member: {} added to study: {}", member.getName(), study.getName());
                belongsToService.save(new BelongsTo(member, study));
            }

            // contributor 삭제됐을 때에 데이터베이스에서 삭제
            else {
                log.info("member: {} removed to study: {}", member.getName(), study.getName());
                belongsToService.deleteByStudyAndMember(study, member);
            }
        }

        // repository
        else {
            // 스터디 레포지토리의 이름 변경 됐을 때에 데이터베이스의 레포이름 변경
            if (response.get("action").equals("renamed")) {
                log.info("study: {}'s repository renamed to {}", study.getName(), repoMap.get("name").toString());
                study.setRepositoryName(repoMap.get("name").toString());
                save(study);
            }

            // 스터디 레포지토리의 삭제됐을 때에 데이터베이스의 스터디 삭제
            else {
                log.info("study: {}'s repository is deleted...Delete study at database, too", study.getName());
                belongsToService.deleteAllByStudy(study);
                studyRepository.delete(study);
            }
        }
    }

    @Transactional
    public void save(Study study) {
        studyRepository.save(study);
    }

    @Transactional
    public Study findById(String id) {
        return studyRepository.findById(id).orElseThrow(NotExistStudyException::new);
    }

    public void checkLeader(User user, Study study) {
        if(!Objects.equals(user.getId(), study.getLeaderId()))
            throw new NotLeaderUserException();
    }

    public void checkAuth(User user, Study study) {
        if (!getMembers(study).contains(user))
            throw new StudyAuthException();
    }

    public void checkSame(User leader, User member) {
        if (leader.equals(member))
            throw new SameUserException();
    }

    public void checkMember(User user, Study study) {
        if (!getMembers(study).contains(user))
            throw new NotExistMemberException();
    }

    //
    // private methods
    //

    private Map<String, Object> createRepositoryResponse(User leader, String repoName) {

        HttpHeaders headers = makeHeader(leader);
        CreateRepository request = new CreateRepository();
        request.setName(repoName);
        request.setAuto_init(true);
        HttpEntity<CreateRepository> entity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://api.github.com/user/repos",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
            return response.getBody();
        } catch (RuntimeException ex) {
            throw new AlreadyExistRepositoryNameException();
        }
    }

    private void deleteRepositoryResponse(User leader, Study study) {

        HttpHeaders headers = makeHeader(leader);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.exchange(
                    "https://api.github.com/repos/" + leader.getName() + "/" + study.getRepositoryName(),
                    HttpMethod.DELETE,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
        } catch (RuntimeException ex) {
            throw new NotExistRepositoryException();
        }
    }

    private void createWebhookResponse(User leader, String repoName) {
        HttpHeaders headers = makeHeader(leader);
        CreateWebhook request = new CreateWebhook();
        request.getConfig().setUrl(this.url);
        HttpEntity<CreateWebhook> entity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(
                "https://api.github.com/repos/" + leader.getName() + "/" + repoName + "/hooks",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                });
    }

    private void addContributorResponse(User leader, User member, Study study) {

        HttpHeaders headers = makeHeader(leader);

        AddContributor request = new AddContributor();
        request.setPermission("admin");

        HttpEntity<AddContributor> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/repos/" + leader.getName() + "/" + study.getRepositoryName() + "/collaborators/" + member.getName(),
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                });
        if(response.getStatusCodeValue() == 204)
            throw new AlreadyExistMemberException();
    }

    private void removeContributorResponse(User leader, User member, Study study) {

        HttpHeaders headers = makeHeader(leader);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.exchange(
                "https://api.github.com/repos/" + leader.getName() + "/" + study.getRepositoryName() + "/collaborators/" + member.getName(),
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {
                });
    }

    private HttpHeaders makeHeader(User owner) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "api-test");
        headers.add("Authorization", "token " + owner.getAccessToken());
        headers.add("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    private List<User> getMembers(Study study) {
        List<User> members = new ArrayList<>();
        List<BelongsTo> belongs = belongsToService.findByStudy(study);

        for (BelongsTo belongsTo : belongs)
            members.add(belongsTo.getMember());

        return members;
    }

    private List<UserInfo> getMemberInfos(List<User> users) {
        List<UserInfo> infos = new ArrayList<>();
        for (User user : users)
            infos.add(new UserInfo(user));
        return infos;
    }

    private List<StudyListInfo> getStudyInfos(User user) {
        List<StudyListInfo> infos = new ArrayList<>();
        List<BelongsTo> belongs = belongsToService.findByMember(user);

        for (BelongsTo belongsTo : belongs)
            infos.add(new StudyListInfo(belongsTo.getStudy()));

        return infos;
    }

    private List<UserInfo> findUserByNameContains(String name, Study study) {
        List<User> users = userService.findByNameContains(name);
        List<User> members = getMembers(study);
        List<UserInfo> infos = new ArrayList<>();

        for (User user : users)
            // 멤버(해당 스터디에 이미 있는 사람)에 속하지 않은 사람으로만 검색
            if (!members.contains(user))
                infos.add(new UserInfo(user));
        return infos;
    }
}
