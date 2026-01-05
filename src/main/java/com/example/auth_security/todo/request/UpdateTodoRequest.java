package com.example.auth_security.todo.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTodoRequest {

    @NotBlank(message = "validation.todo.title.not_blank")
    private String title;

    @NotBlank(message = "validation.todo.description.not_blank")
    private String description;

    @FutureOrPresent(message = "validation.todo.start_date.future_or_present")
    private LocalDate startDate;

    @FutureOrPresent(message = "validation.todo.end_date.future_or_present")
    private LocalDate endDate;

    @NotNull(message = "validation.todo.category.not_null")
    private Long categoryId;

    private LocalTime startTime;
    private LocalTime endTime;

}
