package ru.mentor.service;

import ru.mentor.dto.GetAccessRequest;

public interface AccessService {

    void getCourseAccessToUser(String rqUId, GetAccessRequest request);

    void getModuleAccessToUser(String rqUId, GetAccessRequest request);

    void deleteCourseAccessToUser(String rqUId, GetAccessRequest request);

    void deleteModuleAccessToUser(String rqUId, GetAccessRequest request);

}
