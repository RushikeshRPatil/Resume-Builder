package com.resume.builder.resume_builder.service;

import com.resume.builder.resume_builder.dto.ResumeRequest;
import com.resume.builder.resume_builder.exception.NotFoundException;
import com.resume.builder.resume_builder.model.Resume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeServiceTests {
    private ResumeService resumeService;

    @BeforeEach
    void setUp() {
        resumeService = new ResumeService();
    }

    @Test
    void createsAndFindsResumeById() {
        ResumeRequest request = ResumeRequest.builder()
                .fullName("Rushi Patel")
                .headline("Java Developer")
                .email("rushi@example.com")
                .phone("1234567890")
                .summary("Backend-focused developer")
                .skills(List.of("Java", "Spring Boot"))
                .build();

        Resume created = resumeService.create(request);

        assertNotNull(created.getId());
        Resume fetched = resumeService.findById(created.getId());
        assertEquals("Rushi Patel", fetched.getFullName());
        assertEquals(2, fetched.getSkills().size());
    }

    @Test
    void deletesResumeAndThrowsWhenMissing() {
        ResumeRequest request = ResumeRequest.builder()
                .fullName("User")
                .headline("Role")
                .email("user@example.com")
                .build();

        Resume created = resumeService.create(request);
        resumeService.delete(created.getId());

        assertThrows(NotFoundException.class, () -> resumeService.findById(created.getId()));
    }

    @Test
    void returnsResumesSortedByLastUpdateDesc() throws InterruptedException {
        ResumeRequest first = ResumeRequest.builder()
                .fullName("First")
                .headline("One")
                .email("first@example.com")
                .build();
        ResumeRequest second = ResumeRequest.builder()
                .fullName("Second")
                .headline("Two")
                .email("second@example.com")
                .build();

        resumeService.create(first);
        Thread.sleep(3L);
        resumeService.create(second);

        List<Resume> resumes = resumeService.findAll();
        assertEquals(2, resumes.size());
        assertTrue(resumes.get(0).getUpdatedAt().isAfter(resumes.get(1).getUpdatedAt()));
    }
}
