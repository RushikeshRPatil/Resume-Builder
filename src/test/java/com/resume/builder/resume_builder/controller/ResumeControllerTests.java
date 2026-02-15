package com.resume.builder.resume_builder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.builder.resume_builder.dto.ResumeRequest;
import com.resume.builder.resume_builder.exception.ApiExceptionHandler;
import com.resume.builder.resume_builder.exception.NotFoundException;
import com.resume.builder.resume_builder.model.Resume;
import com.resume.builder.resume_builder.service.ResumeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResumeController.class)
@Import(ApiExceptionHandler.class)
class ResumeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResumeService resumeService;

    @Test
    void createsResume() throws Exception {
        ResumeRequest request = ResumeRequest.builder()
                .fullName("Test User")
                .headline("Developer")
                .email("test@example.com")
                .build();

        Resume response = Resume.builder()
                .id("id-1")
                .fullName("Test User")
                .headline("Developer")
                .email("test@example.com")
                .updatedAt(Instant.now())
                .build();

        Mockito.when(resumeService.create(any(ResumeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/resumes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id-1"));
    }

    @Test
    void listsResumes() throws Exception {
        Resume one = Resume.builder()
                .id("id-1")
                .fullName("User One")
                .headline("Engineer")
                .email("one@example.com")
                .updatedAt(Instant.now())
                .build();

        Mockito.when(resumeService.findAll()).thenReturn(List.of(one));

        mockMvc.perform(get("/api/resumes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("User One"));
    }

    @Test
    void returnsNotFoundOnDelete() throws Exception {
        Mockito.doThrow(new NotFoundException("Resume not found"))
                .when(resumeService).delete(eq("missing-id"));

        mockMvc.perform(delete("/api/resumes/missing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Resume not found"));
    }
}
