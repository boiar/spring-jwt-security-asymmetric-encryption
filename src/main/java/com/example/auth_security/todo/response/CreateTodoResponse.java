package com.example.auth_security.todo.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTodoResponse {

    private Long id;
}
