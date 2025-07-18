package ru.mentor.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Question {

    private Long id;

    private String questionText;

    private String answerText;

    private LocalDateTime createdAt;

}
