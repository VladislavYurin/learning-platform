package ru.mentor.services;

import org.springframework.http.ResponseEntity;
import ru.mentor.dto.front.AccessRequest;

public interface RedirectAccessService {

    ResponseEntity<?> getCourseAccessToUser(AccessRequest request);

    ResponseEntity<?> deleteCourseAccessToUser(AccessRequest request);

    ResponseEntity<?> getModuleAccessToUser(AccessRequest request);

    ResponseEntity<?> deleteModuleAccessToUser(AccessRequest request);

}
