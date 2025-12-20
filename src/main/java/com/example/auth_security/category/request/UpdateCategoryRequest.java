package com.example.auth_security.category.request;

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
public class UpdateCategoryRequest {

    @NotBlank(message = "validation.todo.name.not_blank")
    private String name;

    @NotBlank(message = "validation.todo.description.not_blank")
    private String description;

}
