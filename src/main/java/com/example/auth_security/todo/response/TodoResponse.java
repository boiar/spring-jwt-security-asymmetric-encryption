package com.example.auth_security.todo.response;

import com.example.auth_security.category.response.CategoryResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoResponse {

    private Long id;
    private String userId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean done;
    private CategoryResponse category;


}
