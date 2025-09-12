package ru.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.front.AccessRequest;
import ru.mentor.services.RedirectAccessService;

@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
public class AccessController {

    private final RedirectAccessService redirectAccessService;

    @PostMapping("/course/get-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> getCourseAccessToUser(@RequestBody AccessRequest request) {
        redirectAccessService.getCourseAccessToUser(request);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/course/delete-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> deleteCourseAccessToUser(@RequestBody AccessRequest request) {
        redirectAccessService.deleteCourseAccessToUser(request);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/module/get-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> getModuleAccessToUser(@RequestBody AccessRequest request) {
        redirectAccessService.getModuleAccessToUser(request);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/module/delete-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> deleteModuleAccessToUser(@RequestBody AccessRequest request) {
        redirectAccessService.deleteModuleAccessToUser(request);
        return ResponseEntity.ok().body(null);
    }

}
