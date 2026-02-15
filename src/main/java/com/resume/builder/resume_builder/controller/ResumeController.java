package com.resume.builder.resume_builder.controller;

import com.resume.builder.resume_builder.dto.ResumeRequest;
import com.resume.builder.resume_builder.model.Resume;
import com.resume.builder.resume_builder.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Resume create(@Valid @RequestBody ResumeRequest request) {
        return resumeService.create(request);
    }

    @GetMapping
    public List<Resume> findAll() {
        return resumeService.findAll();
    }

    @GetMapping("/{id}")
    public Resume findById(@PathVariable String id) {
        return resumeService.findById(id);
    }

    @PutMapping("/{id}")
    public Resume update(@PathVariable String id, @Valid @RequestBody ResumeRequest request) {
        return resumeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        resumeService.delete(id);
    }
}
