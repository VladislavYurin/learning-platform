package ru.mentor.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Запрос на управление доступом к курсу")
public class CourseAccessRequest {

    /**
     * Идентификатор пользователя, которому предоставляется или у которого отзывается доступ к курсу.
     */
    @Schema(description = "ID пользователя", example = "123")
    @NotNull
    private Long userId;

    /**
     * Идентификатор курса, к которому предоставляется доступ.
     */
    @Schema(description = "ID курса", example = "456")
    @NotNull
    private Long courseId;

}
