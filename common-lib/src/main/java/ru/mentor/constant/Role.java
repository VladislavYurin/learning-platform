package ru.mentor.constant;

import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.AccessDeniedException;

public enum Role {
    USER,
    ADMIN,
    MENTOR;

    public static Boolean checkIsAdmin(UserEntity user) {
        return user.getRole().equals(Role.ADMIN);
    }

    public static Boolean checkIsMentor(UserEntity user) {
        return user.getRole().equals(Role.MENTOR);
    }

    public static Boolean checkMentorIsAuthorOfCourse(UserEntity user, CourseEntity course) {
        return course.getAuthor().equals(user);
    }

    public static void checkUserIsAdminOrMentor(UserEntity user) {
        if (!checkIsAdmin(user) && !checkIsMentor(user)) {
            throw new AccessDeniedException(String.format(
                    "Юзер с ID = %d не имеет доступа к выдаче доступа к курсам и модулям",
                    user.getId()
            ));
        }
    }

}
