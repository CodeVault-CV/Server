package com.example.algoproject.belongsto.service;

import com.example.algoproject.belongsto.domain.BelongsTo;
import com.example.algoproject.belongsto.repository.BelongsToRepository;
import com.example.algoproject.study.domain.Study;
import com.example.algoproject.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BelongsToService {

    private final BelongsToRepository belongsToRepository;

    @Transactional
    public void save(BelongsTo belongsTo) {
        belongsToRepository.save(belongsTo);
    }

    @Transactional
    public List<BelongsTo> findByStudy(Study study) {
        return belongsToRepository.findByStudy(study);
    }

    @Transactional
    public List<BelongsTo> findByMember(User user) {
        return belongsToRepository.findByMember(user);
    }
}
