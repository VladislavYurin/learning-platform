package ru.mentor.service;

import ru.mentor.dto.GetAccessRequest;

public interface AccessService {

    void getCourseAccessToUser(GetAccessRequest request);

    void getModuleAccessToUser(GetAccessRequest request);

}
