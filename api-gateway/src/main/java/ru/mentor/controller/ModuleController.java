package ru.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.services.RedirectModuleService;

@RestController
@RequestMapping("/module")
@RequiredArgsConstructor
public class ModuleController {

    private final RedirectModuleService redirectModuleService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<ModuleDto> createModule(@RequestBody CreateModuleRequest request) {
        return ResponseEntity.ok().body(redirectModuleService.createModule(request));
    }

    @GetMapping("/{courseId}/{moduleId}")
    public ResponseEntity<ModuleDto> getModuleById(@PathVariable Long courseId, @PathVariable Long moduleId) {
        return ResponseEntity.ok().body(redirectModuleService.getModuleById(courseId, moduleId));
    }

}
