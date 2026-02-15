package com.resume.builder.resume_builder.service;

import com.resume.builder.resume_builder.dto.EducationInput;
import com.resume.builder.resume_builder.dto.ExperienceInput;
import com.resume.builder.resume_builder.dto.ResumeRequest;
import com.resume.builder.resume_builder.exception.NotFoundException;
import com.resume.builder.resume_builder.model.Education;
import com.resume.builder.resume_builder.model.Experience;
import com.resume.builder.resume_builder.model.Resume;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final Map<String, Resume> store = new ConcurrentHashMap<>();

    public Resume create(ResumeRequest request) {
        String id = UUID.randomUUID().toString();
        Resume resume = toResume(id, request);
        store.put(id, resume);
        return resume;
    }

    public List<Resume> findAll() {
        return store.values().stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    public Resume findById(String id) {
        Resume resume = store.get(id);
        if (resume == null) {
            throw new NotFoundException("Resume not found for id: " + id);
        }
        return resume;
    }

    public Resume update(String id, ResumeRequest request) {
        if (!store.containsKey(id)) {
            throw new NotFoundException("Resume not found for id: " + id);
        }
        Resume updated = toResume(id, request);
        store.put(id, updated);
        return updated;
    }

    public void delete(String id) {
        Resume removed = store.remove(id);
        if (removed == null) {
            throw new NotFoundException("Resume not found for id: " + id);
        }
    }

    private Resume toResume(String id, ResumeRequest request) {
        return Resume.builder()
                .id(id)
                .fullName(request.getFullName().trim())
                .headline(request.getHeadline().trim())
                .email(request.getEmail().trim())
                .phone(trimOrNull(request.getPhone()))
                .summary(trimOrNull(request.getSummary()))
                .skills(cleanSkills(request.getSkills()))
                .educations(mapEducations(request.getEducations()))
                .experiences(mapExperiences(request.getExperiences()))
                .updatedAt(Instant.now())
                .build();
    }

    private List<String> cleanSkills(List<String> rawSkills) {
        if (rawSkills == null) {
            return new ArrayList<>();
        }
        return rawSkills.stream()
                .map(this::trimOrNull)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Education> mapEducations(List<EducationInput> inputs) {
        if (inputs == null) {
            return new ArrayList<>();
        }
        return inputs.stream()
                .map(input -> Education.builder()
                        .school(input.getSchool().trim())
                        .degree(input.getDegree().trim())
                        .startDate(trimOrNull(input.getStartDate()))
                        .endDate(trimOrNull(input.getEndDate()))
                        .description(trimOrNull(input.getDescription()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<Experience> mapExperiences(List<ExperienceInput> inputs) {
        if (inputs == null) {
            return new ArrayList<>();
        }
        return inputs.stream()
                .map(input -> Experience.builder()
                        .company(input.getCompany().trim())
                        .role(input.getRole().trim())
                        .startDate(trimOrNull(input.getStartDate()))
                        .endDate(trimOrNull(input.getEndDate()))
                        .description(trimOrNull(input.getDescription()))
                        .build())
                .collect(Collectors.toList());
    }

    private String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
