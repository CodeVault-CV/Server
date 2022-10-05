package com.example.algoproject.tag.service;

import com.example.algoproject.errors.exception.notfound.NotExistTagException;
import com.example.algoproject.tag.domain.Tag;
import com.example.algoproject.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public void save(Tag tag) {
        tagRepository.save(tag);
    }

    @Transactional(readOnly = true)
    public Tag findById(Long id) {
        return tagRepository.findById(id).orElseThrow(NotExistTagException::new);
    }
}
