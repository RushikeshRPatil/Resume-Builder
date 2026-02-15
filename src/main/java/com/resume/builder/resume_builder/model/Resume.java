package com.resume.builder.resume_builder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    private String id;
    private String fullName;
    private String headline;
    private String email;
    private String phone;
    private String summary;
    @Builder.Default
    private List<String> skills = new ArrayList<>();
    @Builder.Default
    private List<Education> educations = new ArrayList<>();
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();
    private Instant updatedAt;
}
