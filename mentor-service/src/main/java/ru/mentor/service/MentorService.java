package ru.mentor.service;

import ru.mentor.dto.UserInfoDto;
import java.util.List;
public interface MentorService {

    /**
     * Возвращает список всех пользоваталей с ролью MENTOR.
     */
    List<UserInfoDto> getAllMentors();
}
