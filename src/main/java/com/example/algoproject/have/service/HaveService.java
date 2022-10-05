package com.example.algoproject.have.service;

import com.example.algoproject.have.domain.Have;
import com.example.algoproject.have.repository.HaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class HaveService {

    private final HaveRepository haveRepository;

    @Transactional
    public void save(Have have) {
        haveRepository.save(have);
    }
}
