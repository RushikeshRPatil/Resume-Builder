package com.resume.builder.resume_builder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationInput {
    @NotBlank(message = "School is required")
    private String school;

    @NotBlank(message = "Degree is required")
    private String degree;

    private String startDate;
    private String endDate;
    private String description;
}
