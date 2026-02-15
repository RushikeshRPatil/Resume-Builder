package com.resume.builder.resume_builder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    private String school;
    private String degree;
    private String startDate;
    private String endDate;
    private String description;
}
