package ru.mentor.services;

import ru.mentor.dto.front.AccessRequest;

public interface RedirectAccessService {

    void getCourseAccessToUser(AccessRequest request);

    void deleteCourseAccessToUser(AccessRequest request);

    void getModuleAccessToUser(AccessRequest request);

    void deleteModuleAccessToUser(AccessRequest request);

}
