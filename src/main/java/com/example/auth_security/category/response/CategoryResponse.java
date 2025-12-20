package com.example.auth_security.category.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private int todosCount;
    private LocalDateTime createdDate;
    private LocalDateTime updatedData;

}
