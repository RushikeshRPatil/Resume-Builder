package com.resume.builder.resume_builder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Headline is required")
    private String headline;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private String phone;

    @Size(max = 1000, message = "Summary cannot exceed 1000 characters")
    private String summary;

    @Builder.Default
    private List<@NotBlank(message = "Skill entries cannot be blank") String> skills = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<EducationInput> educations = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<ExperienceInput> experiences = new ArrayList<>();
}
