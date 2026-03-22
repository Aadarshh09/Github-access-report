package com.cloudeagle.githubaccess.controller;

import com.cloudeagle.githubaccess.model.AccessReport;
import com.cloudeagle.githubaccess.service.AccessReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing the GitHub access report API.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccessReportController {
    public AccessReportController(AccessReportService accessReportService) {
        this.accessReportService = accessReportService;
    }

    private final AccessReportService accessReportService;

    /**
     * GET /api/v1/access-report?org={orgName}
     *
     * Returns a JSON report showing which users have access
     * to which repositories within the given GitHub organization.
     *
     * @param org GitHub organization name (required)
     * @return AccessReport with user → repo mappings
     */
    @GetMapping("/access-report")
    public ResponseEntity<AccessReport> getAccessReport(
            @RequestParam(name = "org") String org) {

        System.out.println("Received access report request for org: " + org);
        AccessReport report = accessReportService.generateReport(org);
        return ResponseEntity.ok(report);
    }
}
