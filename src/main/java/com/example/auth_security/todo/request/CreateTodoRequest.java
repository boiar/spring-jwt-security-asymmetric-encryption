package com.example.auth_security.todo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTodoRequest {

    @NotBlank(message = "validation.todo.title.not_blank")
    @Schema(example = "Todo title")
    private String title;

    @NotBlank(message = "validation.todo.description.not_blank")
    @Schema(example = "Todo Desc ...")
    private String description;

    @FutureOrPresent(message = "validation.todo.start_date.future_or_present")
    @Schema(example = "2025-12-28")
    private LocalDate startDate;

    @FutureOrPresent(message = "validation.todo.end_date.future_or_present")
    @Schema(example = "2025-12-30")
    private LocalDate endDate;

    private LocalTime startTime;
    private LocalTime endTime;
    private Long categoryId;



}
